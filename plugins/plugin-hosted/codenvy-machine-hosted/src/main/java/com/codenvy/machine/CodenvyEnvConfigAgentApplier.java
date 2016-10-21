/*
 *  [2012] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
