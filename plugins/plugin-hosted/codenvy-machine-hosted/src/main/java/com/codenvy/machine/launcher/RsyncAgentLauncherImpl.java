/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.machine.launcher;

import com.codenvy.machine.RemoteDockerNode;

import org.eclipse.che.api.agent.server.launcher.AbstractAgentLauncher;
import org.eclipse.che.api.agent.shared.model.Agent;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.machine.server.spi.Instance;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Starts ws agent in the machine and waits until ws agent sends notification about its start.
 *
 * @author Alexander Garagatyi
 * @author Anatolii Bazko
 */
@Singleton
public class RsyncAgentLauncherImpl extends AbstractAgentLauncher {
    @Inject
    public RsyncAgentLauncherImpl(@Named("machine.agent.max_start_time_ms") long agentMaxStartTimeMs,
                                  @Named("machine.agent.ping_delay_ms") long agentPingDelayMs) {
        super(agentMaxStartTimeMs, agentPingDelayMs, new NoOpAgentLaunchingChecker());
    }

    @Override
    public String getAgentName() {
        return "org.eclipse.che.rsync";
    }

    @Override
    public String getMachineType() {
        return "docker";
    }

    @Override
    public void launch(Instance machine, Agent agent) throws ServerException {
        // TODO move pubkey from env of container to agent
        super.launch(machine, agent);

        RemoteDockerNode node = (RemoteDockerNode)machine.getNode();
        node.bindWorkspace();
    }
}
