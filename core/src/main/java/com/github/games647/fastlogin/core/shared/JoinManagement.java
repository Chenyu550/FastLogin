/*
 * SPDX-License-Identifier: MIT
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 games647 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.games647.fastlogin.core.shared;

import com.github.games647.craftapi.model.Profile;
import com.github.games647.craftapi.resolver.RateLimitException;
import com.github.games647.fastlogin.core.hooks.AuthPlugin;
import com.github.games647.fastlogin.core.hooks.bedrock.BedrockService;
import com.github.games647.fastlogin.core.shared.event.FastLoginPreLoginEvent;
import com.github.games647.fastlogin.core.storage.StoredProfile;
import net.md_5.bungee.config.Configuration;

import java.util.Optional;

public abstract class JoinManagement<P extends C, C, S extends LoginSource> {

    protected final FastLoginCore<P, C, ?> core;
    protected final AuthPlugin<P> authHook;
    private final BedrockService<?> bedrockService;

    public JoinManagement(FastLoginCore<P, C, ?> core, AuthPlugin<P> authHook, BedrockService<?> bedrockService) {
        this.core = core;
        this.authHook = authHook;
        this.bedrockService = bedrockService;
    }

    public void onLogin(String username, S source) {
        //check if the player is connecting through Bedrock Edition
        if (bedrockService != null && bedrockService.isBedrockConnection(username)) {
            //perform Bedrock specific checks and skip Java checks if no longer needed
            if (bedrockService.performChecks(username, source)) {
                return;
            }
        }

        StoredProfile profile = core.getStorage().loadProfile(username);
        //can't be a premium Java player, if it's not saved in the database
        if (profile == null) {
            return;
        }

        if (profile.isFloodgateMigrated()) {
            if (profile.getFloodgate() == FloodgateState.TRUE) {
                // migrated and enabled floodgate player, however the above bedrocks fails, so the current connection
                // isn't premium
                return;
            }
        } else {
            profile.setFloodgate(FloodgateState.FALSE);
            core.getPlugin().getLog().info(
                    "Player {} will be migrated to the v2 database schema as a JAVA user", username
            );
        }

        callFastLoginPreLoginEvent(username, source, profile);

        String ip = source.getAddress().getAddress().getHostAddress();
        profile.setLastIp(ip);
        if (profile.isExistingPlayer()) {
            if (profile.isOnlinemodePreferred()) {
                core.getPlugin().getLog().info("正在为已注册玩家: {} 请求正版登录", username);
                requestPremiumLogin(source, profile, username, true);
            } else {
                if (isValidUsername(source, profile)) {
                    startCrackedSession(source, profile, username);
                }
            }
        } else {
            performNewPlayerLogin(username, source, ip, profile);
        }
    }

    private void performNewPlayerLogin(String username, S source, String ip, StoredProfile profile) {
        try {
            if (core.hasFailedLogin(ip, username)) {
                core.getPlugin().getLog().info("第二次尝试登录 -> 将 {} 视为离线玩家", username);

                //first login request failed so make a cracked session
                startCrackedSession(source, profile, username);
                return;
            }

            Configuration config = core.getConfig();
            Optional<Profile> premiumUUID = Optional.empty();
            if (config.get("nameChangeCheck", false) || config.get("autoRegister", false)) {
                premiumUUID = core.getResolver().findProfile(username);
            }

            if (!premiumUUID.isPresent()
                    || (!isNameChanged(source, username, premiumUUID.get())
                    && !isUsernameAvailable(source, username, profile))) {
                //nothing detected the player as premium -> start a cracked session
                if (core.getConfig().get("switchMode", false)) {
                    source.kick(core.getMessage("switch-kick-message"));
                    return;
                }

                startCrackedSession(source, profile, username);
            }
        } catch (RateLimitException rateLimitEx) {
            core.getPlugin().getLog().error("已达到 Mojang 针对 {} 的速率限制。此服务器的公共 "
                    + "IPv4 地址在 10 分钟内发出了超过 600 次名称转 UUID 请求。10 分钟"
                    + "后，我们才能再次发出请求。", username);
        } catch (Exception ex) {
            core.getPlugin().getLog().error("Failed to check premium state of {}", username, ex);
        }
    }

    protected boolean isValidUsername(LoginSource source, StoredProfile profile) {
        if (bedrockService != null && bedrockService.isUsernameForbidden(profile)) {
            core.getPlugin().getLog().info("在离线的玩家名里检测到 Floodgate 前缀");
            source.kick("你的用户名包含非法字符或前缀");
            return false;
        }

        return true;
    }

    private boolean isUsernameAvailable(S source, String username, StoredProfile profile) throws Exception {
        core.getPlugin().getLog().info("玩家名 {} 是一个正版账号的名字", username);
        if (core.getConfig().get("autoRegister", false) && (authHook == null || !authHook.isRegistered(username))) {
            requestPremiumLogin(source, profile, username, false);
            return true;
        }

        return false;
    }

    private boolean isNameChanged(S source, String username, Profile profile) {
        //user not exists in the db
        if (core.getConfig().get("nameChangeCheck", false)) {
            StoredProfile storedProfile = core.getStorage().loadProfile(profile.getId());
            if (storedProfile != null) {
                if (storedProfile.getFloodgate() == FloodgateState.TRUE) {
                    core.getPlugin().getLog()
                            .info("玩家 {} 已被 FastLogin 记录为基岩版玩家。", username);
                    return false;
                }

                //uuid exists in the database
                core.getPlugin().getLog().info("游戏资料 {} 更改了其用户名", profile);

                //update the username to the new one in the database
                storedProfile.setPlayerName(username);

                requestPremiumLogin(source, storedProfile, username, false);
                return true;
            }
        }

        return false;
    }

    public abstract FastLoginPreLoginEvent callFastLoginPreLoginEvent(String username, S source, StoredProfile profile);

    public abstract void requestPremiumLogin(S source, StoredProfile profile, String username, boolean registered);

    public abstract void startCrackedSession(S source, StoredProfile profile, String username);
}
