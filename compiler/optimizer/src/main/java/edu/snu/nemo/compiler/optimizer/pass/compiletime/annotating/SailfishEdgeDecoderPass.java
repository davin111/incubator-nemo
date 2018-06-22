/*
 * Copyright (C) 2018 Seoul National University
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
package edu.snu.nemo.compiler.optimizer.pass.compiletime.annotating;

import edu.snu.nemo.common.coder.BytesDecoderFactory;
import edu.snu.nemo.common.dag.DAG;
import edu.snu.nemo.common.ir.edge.IREdge;
import edu.snu.nemo.common.ir.edge.executionproperty.DataCommunicationPatternProperty;
import edu.snu.nemo.common.ir.edge.executionproperty.DecoderProperty;
import edu.snu.nemo.common.ir.vertex.IRVertex;

import java.util.Collections;
import java.util.List;

/**
 * A pass to support Sailfish-like shuffle by tagging edges.
 * This pass modifies the decoder property toward {@link edu.snu.nemo.common.ir.vertex.transform.RelayTransform}
 * to read data as byte arrays.
 */
public final class SailfishEdgeDecoderPass extends AnnotatingPass {
  /**
   * Default constructor.
   */
  public SailfishEdgeDecoderPass() {
    super(DecoderProperty.class, Collections.singleton(DataCommunicationPatternProperty.class));
  }

  @Override
  public DAG<IRVertex, IREdge> apply(final DAG<IRVertex, IREdge> dag) {
    dag.getVertices().forEach(vertex -> {
      final List<IREdge> inEdges = dag.getIncomingEdgesOf(vertex);
      inEdges.forEach(edge -> {
        if (edge.getPropertyValue(DataCommunicationPatternProperty.class).get()
            .equals(DataCommunicationPatternProperty.Value.Shuffle)) {
          edge.setProperty(DecoderProperty.of(BytesDecoderFactory.of()));
        }
      });
    });
    return dag;
  }
}
