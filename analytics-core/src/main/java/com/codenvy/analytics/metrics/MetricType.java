/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

package com.codenvy.analytics.metrics;

/**
 * Predefined metrics.
 *
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public enum MetricType {
    CREATED_WORKSPACES,
    DESTROYED_WORKSPACES,
    TOTAL_WORKSPACES,
    ACTIVE_WORKSPACES,
    NON_ACTIVE_WORKSPACES,
    NEW_ACTIVE_WORKSPACES,
    RETURNING_ACTIVE_WORKSPACES,
    ACTIVE_WORKSPACES_LIST,

    CREATED_USERS,
    CREATED_USERS_FROM_FACTORY,
    CREATED_USERS_FROM_AUTH,
    REMOVED_USERS,
    TOTAL_USERS,
    ACTIVE_USERS,
    ACTIVE_USERS_LIST,
    NON_ACTIVE_USERS,
    NEW_ACTIVE_USERS,
    RETURNING_ACTIVE_USERS,

    PROJECT_TYPES,
    PROJECT_TYPE_JAR,
    PROJECT_TYPE_WAR,
    PROJECT_TYPE_JSP,
    PROJECT_TYPE_SPRING,
    PROJECT_TYPE_PHP,
    PROJECT_TYPE_DJANGO,
    PROJECT_TYPE_PYTHON,
    PROJECT_TYPE_JAVASCRIPT,
    PROJECT_TYPE_RUBY,
    PROJECT_TYPE_MMP,
    PROJECT_TYPE_NODEJS,
    PROJECT_TYPE_ANDROID,
    PROJECT_TYPE_OTHERS,

    PRODUCT_USAGE_TIME_BELOW_1_MIN,
    PRODUCT_USAGE_TIME_BETWEEN_1_AND_10_MIN,
    PRODUCT_USAGE_TIME_BETWEEN_10_AND_60_MIN,
    PRODUCT_USAGE_TIME_ABOVE_60_MIN,
    PRODUCT_USAGE_TIME_TOTAL,
    PRODUCT_USAGE_SESSIONS_BELOW_1_MIN,
    PRODUCT_USAGE_SESSIONS_BETWEEN_1_AND_10_MIN,
    PRODUCT_USAGE_SESSIONS_BETWEEN_10_AND_60_MIN,
    PRODUCT_USAGE_SESSIONS_ABOVE_60_MIN,
    PRODUCT_USAGE_SESSIONS_TOTAL,

    PROJECT_CREATED,
    PROJECT_DEPLOYED_TYPES,
    PROJECT_PAAS_ANY,
    PROJECT_PAAS_AWS,
    PROJECT_PAAS_APPFOG,
    PROJECT_PAAS_CLOUDBEES,
    PROJECT_PAAS_CLOUDFOUNDRY,
    PROJECT_PAAS_GAE,
    PROJECT_PAAS_HEROKU,
    PROJECT_PAAS_OPENSHIFT,
    PROJECT_PAAS_TIER3,
    PROJECT_PAAS_MANYAMO,
    PROJECT_NO_PAAS_DEFINED
}

