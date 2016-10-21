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
package com.codenvy.api.deploy;

import org.eclipse.che.commons.lang.IoUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Singleton
public class PubKeyEnvProvider implements Provider<String> {

    private static String PUB_KEY_ENV_VAR_NAME = "PUB_KEY";

    private String pubKeyEnvVar;

    @Inject
    public PubKeyEnvProvider(@Named("workspace.backup.public_key_path") String pubKeyPath)
            throws IOException {

        pubKeyEnvVar = PUB_KEY_ENV_VAR_NAME + "=" +
                       IoUtil.readAndCloseQuietly(new BufferedInputStream(new FileInputStream(new File(pubKeyPath))));
    }

    @Override
    public String get() {
        return pubKeyEnvVar;
    }
}
