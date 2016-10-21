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
package com.codenvy.machine;

import org.eclipse.che.api.agent.server.exception.AgentException;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.environment.server.AgentConfigApplier;
import org.eclipse.che.api.environment.server.EnvConfigAgentApplier;
import org.eclipse.che.api.environment.server.model.CheServicesEnvironmentImpl;

import javax.inject.Inject;

/**
 * @author Alexander Garagatyi
 */
public class CodenvyEnvConfigAgentApplier extends EnvConfigAgentApplier {
    @Inject
    public CodenvyEnvConfigAgentApplier(AgentConfigApplier agentConfigApplier) {
        super(agentConfigApplier);
    }

    @Override
    public void apply(Environment envConf,
                      CheServicesEnvironmentImpl internalEnv) throws AgentException {


        super.apply(envConf, internalEnv);
    }
}
