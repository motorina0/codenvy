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

import org.eclipse.che.api.agent.server.launcher.AgentLaunchingChecker;
import org.eclipse.che.api.agent.shared.model.Agent;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.server.spi.Instance;
import org.eclipse.che.api.machine.server.spi.InstanceProcess;

/**
 * @author Alexander Garagatyi
 */
public class NoOpAgentLaunchingChecker implements AgentLaunchingChecker {
    @Override
    public boolean isLaunched(Agent agent, InstanceProcess process, Instance machine) throws MachineException {
        return true;
    }
}
