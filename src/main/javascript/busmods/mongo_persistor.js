/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This is just a wrapper around the Java persistor

var j_pers = new org.vertx.java.busmods.persistor.MongoPersistor();
j_pers.setVertx(org.vertx.java.deploy.impl.VertxLocator.vertx);
j_pers.setContainer(org.vertx.java.deploy.impl.VertxLocator.container);

j_pers.start();

function vertxStop() {
  j_pers.stop();
}
