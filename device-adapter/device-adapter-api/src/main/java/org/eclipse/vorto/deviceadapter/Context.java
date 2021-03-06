/**
 * Copyright (c) 2015-2018 Bosch Software Innovations GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * The Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Bosch Software Innovations GmbH - Please refer to git log
 */
package org.eclipse.vorto.deviceadapter;

import org.eclipse.vorto.model.Infomodel;

/**
 * Context to be passed with callbacks to the application, to help interpret the result.
 */
class Context {

   private String deviceId;
   private Infomodel infomodel;
   
   /**
    * Instantiates a new Context.
    *
    * @param deviceID           the device id
    * @param infomodel          the infomodel
    */
   public Context(String deviceId, Infomodel infomodel) {
      this.deviceId = deviceId;
      this.infomodel = infomodel;
   }

   /**
    * Gets device id.
    *
    * @return the device id
    */
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * Gets infomodel.
    *
    * @return the infomodel
    */
   public Infomodel getInfomodel() {
      return infomodel;
   }

}
