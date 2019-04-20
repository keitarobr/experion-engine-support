package br.ufsc.ppgcc.experion.extractor.algorithm.keygraph;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ClusterVisualizations {

    public static void viewGraph(ArrayList<DocumentCluster> clusters) {

        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane pane = new JTabbedPane();
        frame.setContentPane(pane);


        for (DocumentCluster cluster : clusters) {
            Graph<String, Integer> g = new SparseMultigraph<>();
            int edgeId = 1;

            for (Node node : cluster.keyGraph.values()) {
                g.addVertex(node.keyword.word);
            }

            for (Node node : cluster.keyGraph.values()) {
                for (Edge e : node.edges.values()) {
                    if (e.n1.equals(node)) {
                        try {
                            g.addEdge(edgeId++, node.keyword.word, e.n2.keyword.word);
                        } catch (Exception e3) {

                        }
                    }
                }
            }
            Layout<String, Integer> layout = new CircleLayout<>(g);
            layout.setSize(new Dimension(900,900)); // sets the initial size of the space
            BasicVisualizationServer<String,Integer> vv =
                    new BasicVisualizationServer<String,Integer>(layout);
            vv.setPreferredSize(new Dimension(900,900)); //Sets the viewing area size
            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.setDoubleBuffered(true);

            pane.addTab("g", vv);
//            frame.getContentPane().add(vv);


        }

        //SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
        // The Layout<V, E> is parameterized by the vertex and edge types


//        Layout<String, Integer> layout = new SpringLayout<>(g);
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        frame.pack();
        frame.setVisible(true);

    }

}
