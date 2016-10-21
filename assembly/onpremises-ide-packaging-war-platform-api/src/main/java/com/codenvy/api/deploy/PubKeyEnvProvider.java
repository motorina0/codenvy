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
