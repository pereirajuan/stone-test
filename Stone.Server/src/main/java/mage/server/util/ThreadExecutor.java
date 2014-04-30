/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.server.util;

import java.util.concurrent.*;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class ThreadExecutor {

    private static final ExecutorService callExecutor = Executors.newCachedThreadPool();
    private static final ExecutorService gameExecutor = Executors.newFixedThreadPool(ConfigSettings.getInstance().getMaxGameThreads());
    private static final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(5);

    /**
     * noxx: what the settings below do is setting the ability to keep OS threads for new games for 60 seconds
     * If there is no new game created within this time period, the thread may be discarded.
     * But anyway if new game is created later, new OS/java thread will be created for it
     * taking MaxGameThreads limit into account.
     *
     * This all is done for performance reasons as creating new OS threads is resource consuming process.
     */
    static {
        ((ThreadPoolExecutor)callExecutor).setKeepAliveTime(60, TimeUnit.SECONDS);
        ((ThreadPoolExecutor)callExecutor).allowCoreThreadTimeOut(true);
        ((ThreadPoolExecutor)gameExecutor).setKeepAliveTime(60, TimeUnit.SECONDS);
        ((ThreadPoolExecutor)gameExecutor).allowCoreThreadTimeOut(true);
        ((ThreadPoolExecutor)timeoutExecutor).setKeepAliveTime(60, TimeUnit.SECONDS);
        ((ThreadPoolExecutor)timeoutExecutor).allowCoreThreadTimeOut(true);
    }

    private static final ThreadExecutor INSTANCE = new ThreadExecutor();

    public static ThreadExecutor getInstance() {
        return INSTANCE;
    }

    private ThreadExecutor() {}

    public ExecutorService getCallExecutor() {
        return callExecutor;
    }

    public ExecutorService getGameExecutor() {
        return gameExecutor;
    }

    public ScheduledExecutorService getTimeoutExecutor() {
        return timeoutExecutor;
    }

}
