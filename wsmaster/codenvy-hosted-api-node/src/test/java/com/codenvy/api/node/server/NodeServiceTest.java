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
package com.codenvy.api.node.server;

import org.testng.Assert;
import org.testng.annotations.Test;

public class NodeServiceTest {


    @Test
    public void checkRegexp() {

        NodeService nodeService = new NodeService();
        String lineBefore = "$machine_extra_hosts=\"abcd:1234\"";
        String lineAfter = nodeService.updateLine("machine_extra_hosts", lineBefore,"efgh:5678");

        Assert.assertEquals(lineAfter, "$machine_extra_hosts = \"abcd:1234,efgh:5678\"");
    }
}
