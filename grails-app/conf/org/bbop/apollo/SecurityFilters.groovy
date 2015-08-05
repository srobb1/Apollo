package org.bbop.apollo

import org.apache.shiro.SecurityUtils

/**
 * Generated by the Shiro plugin. This filters class protects all URLs
 * via access control by convention.
 */
class SecurityFilters {

    def permissionService

    int count = 0

    def filters = {
        all(uri: "/**") {
            before = {
                if (request.getSession(false) == null) {
                    println "name "
                    println "is: " + request.getUserPrincipal()
//                    permissionService.sendLogout(SecurityUtils.subject?.principal)
                    count = 0
                    println "${count} no session present ${session.getAttribute("username")}"
                } else {
                    ++count
                    println "${count} Good to go!"

                    if (session.getAttribute("username") == null) {
                        println "${count} no username present "
                    } else {
                        println "${count} is pressent . . ${session.getAttributeNames()}"

//                        log.debug "LOGOUT SESSION ${SecurityUtils?.subject?.getSession(false)?.id}"
                        println "count ${count} ${count % 100}"
                        if (count > 0 && count % 100 == 0) {
//                            permissionService.sendLogout(SecurityUtils.subject?.principal)
                            count = 0
//                            SecurityUtils.subject.logout()
                        }

                    }
                }

                // validate everyone right now
//                return  true

                // Ignore direct views (e.g. the default main index page).
                if (!controllerName) return true
//
//                // Access control by convention.
//                accessControl(auth:false)
            }
        }
    }
}
