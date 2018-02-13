/*
 * Copyright (C) 2017 Seoul National University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.snu.coral.compiler.backend.coral;

import edu.snu.coral.common.dag.DAG;
import edu.snu.coral.compiler.backend.Backend;
import edu.snu.coral.common.ir.edge.IREdge;
import edu.snu.coral.common.ir.vertex.IRVertex;
import edu.snu.coral.runtime.common.RuntimeIdGenerator;
import edu.snu.coral.runtime.common.plan.physical.PhysicalPlan;
import edu.snu.coral.runtime.common.plan.physical.PhysicalPlanGenerator;
import edu.snu.coral.runtime.common.plan.physical.PhysicalStage;
import edu.snu.coral.runtime.common.plan.physical.PhysicalStageEdge;
import org.apache.reef.tang.Tang;

/**
 * Backend component for Coral Runtime.
 */
public final class CoralBackend implements Backend<PhysicalPlan> {
  /**
   * Constructor.
   */
  public CoralBackend() {
  }

  /**
   * Compiles an IR DAG into a {@link PhysicalPlan} to be submitted to Runtime.
   * @param irDAG to compile.
   * @return the execution plan to be submitted to Runtime.
   * @throws Exception any exception occurred during the compilation.
   */
  public PhysicalPlan compile(final DAG<IRVertex, IREdge> irDAG) throws Exception {
    final PhysicalPlanGenerator physicalPlanGenerator =
        Tang.Factory.getTang().newInjector().getInstance(PhysicalPlanGenerator.class);
    return compile(irDAG, physicalPlanGenerator);
  }

  /**
   * Compiles an IR DAG into a {@link PhysicalPlan} to be submitted to Runtime.
   * Receives {@link PhysicalPlanGenerator} with configured directory of DAG files.
   * @param irDAG to compile.
   * @param physicalPlanGenerator with custom DAG directory.
   * @return the execution plan to be submitted to Runtime.
   */
  public PhysicalPlan compile(final DAG<IRVertex, IREdge> irDAG,
                              final PhysicalPlanGenerator physicalPlanGenerator) {
    final DAG<PhysicalStage, PhysicalStageEdge> physicalStageDAG = irDAG.convert(physicalPlanGenerator);
    final PhysicalPlan physicalPlan = new PhysicalPlan(RuntimeIdGenerator.generatePhysicalPlanId(),
        physicalStageDAG, physicalPlanGenerator.getTaskIRVertexMap());
    return physicalPlan;
  }
}