/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.domain.semiflows;

/**
 *
 * @author elvio
 */
public class Test_BridgeAndVehicles_V04_P05_N02_unf {
        public static FlowsGenerator initBridgeAndVehicles_V04_P05_N02_unf() {
        int M=52, N=28;
        FlowsGenerator fg = new FlowsGenerator(N, N, M, PTFlows.Type.PLACE_SEMIFLOWS);
        final int flows[][] = {
            { 0, 0, 1 }, { 1, 0, 1 }, { 2, 0, -1 }, { 6, 0, -1 }, { 0, 1, 1 }, { 2, 1, 1 }, { 3, 1, -1 }, { 6, 1, -1 }, 
            { 0, 2, 1 }, { 3, 2, 1 }, { 4, 2, -1 }, { 6, 2, -1 }, { 0, 3, 1 }, { 4, 3, 1 }, { 5, 3, -1 }, { 6, 3, -1 }, 
            { 0, 4, 1 }, { 5, 4, 1 }, { 1, 4, -1 }, { 6, 4, -1 }, { 7, 5, 1 }, { 8, 5, -1 }, { 9, 5, -1 }, { 9, 6, 1 }, 
            { 11, 6, 1 }, { 13, 6, 1 }, { 17, 6, 1 }, { 12, 6, -1 }, { 19, 6, -1 }, { 20, 6, -1 }, { 9, 7, 1 }, 
            { 11, 7, 1 }, { 14, 7, 1 }, { 17, 7, 1 }, { 13, 7, -1 }, { 19, 7, -1 }, { 20, 7, -1 }, { 9, 8, 1 }, 
            { 11, 8, 1 }, { 15, 8, 1 }, { 17, 8, 1 }, { 14, 8, -1 }, { 19, 8, -1 }, { 20, 8, -1 }, { 9, 9, 1 }, 
            { 11, 9, 1 }, { 16, 9, 1 }, { 17, 9, 1 }, { 15, 9, -1 }, { 19, 9, -1 }, { 20, 9, -1 }, { 9, 10, 5 }, 
            { 21, 10, 1 }, { 9, 10, -5 }, { 18, 10, -1 }, { 9, 11, 5 }, { 22, 11, 1 }, { 9, 11, -5 }, { 19, 11, -1 }, 
            { 20, 12, 1 }, { 9, 12, -1 }, { 23, 12, -1 }, { 12, 13, 1 }, { 24, 13, 1 }, { 13, 13, -1 }, { 17, 13, -1 }, 
            { 13, 14, 1 }, { 24, 14, 1 }, { 14, 14, -1 }, { 17, 14, -1 }, { 14, 15, 1 }, { 24, 15, 1 }, { 15, 15, -1 }, 
            { 17, 15, -1 }, { 15, 16, 1 }, { 24, 16, 1 }, { 16, 16, -1 }, { 17, 16, -1 }, { 16, 17, 1 }, { 24, 17, 1 }, 
            { 12, 17, -1 }, { 17, 17, -1 }, { 18, 18, 1 }, { 25, 18, 1 }, { 10, 18, -1 }, { 26, 18, -1 }, { 18, 19, 1 }, 
            { 26, 19, 1 }, { 10, 19, -1 }, { 27, 19, -1 }, { 19, 20, 1 }, { 25, 20, 1 }, { 11, 20, -1 }, { 26, 20, -1 }, 
            { 19, 21, 1 }, { 26, 21, 1 }, { 11, 21, -1 }, { 27, 21, -1 }, { 18, 22, 1 }, { 27, 22, 1 }, { 22, 22, -1 }, 
            { 25, 22, -1 }, { 19, 23, 1 }, { 27, 23, 1 }, { 21, 23, -1 }, { 25, 23, -1 }, { 1, 24, 1 }, { 10, 24, 1 }, 
            { 13, 24, 1 }, { 25, 24, 1 }, { 1, 24, -1 }, { 13, 24, -1 }, { 22, 24, -1 }, { 25, 24, -1 }, { 1, 25, 1 }, 
            { 10, 25, 1 }, { 13, 25, 1 }, { 26, 25, 1 }, { 1, 25, -1 }, { 13, 25, -1 }, { 22, 25, -1 }, { 25, 25, -1 },
            { 1, 26, 1 }, { 10, 26, 1 }, { 13, 26, 1 }, { 27, 26, 1 }, { 1, 26, -1 }, { 13, 26, -1 }, { 22, 26, -1 }, 
            { 25, 26, -1 }, { 1, 27, 1 }, { 10, 27, 1 }, { 14, 27, 1 }, { 25, 27, 1 }, { 1, 27, -1 }, { 14, 27, -1 }, 
            { 22, 27, -1 }, { 25, 27, -1 }, { 1, 28, 1 }, { 10, 28, 1 }, { 14, 28, 1 }, { 26, 28, 1 }, { 1, 28, -1 }, 
            { 14, 28, -1 }, { 22, 28, -1 }, { 25, 28, -1 }, { 1, 29, 1 }, { 10, 29, 1 }, { 14, 29, 1 }, { 27, 29, 1 }, 
            { 1, 29, -1 }, { 14, 29, -1 }, { 22, 29, -1 }, { 25, 29, -1 }, { 1, 30, 1 }, { 10, 30, 1 }, { 15, 30, 1 }, 
            { 25, 30, 1 }, { 1, 30, -1 }, { 15, 30, -1 }, { 22, 30, -1 }, { 25, 30, -1 }, { 1, 31, 1 }, { 10, 31, 1 }, 
            { 15, 31, 1 }, { 26, 31, 1 }, { 1, 31, -1 }, { 15, 31, -1 }, { 22, 31, -1 }, { 25, 31, -1 }, { 1, 32, 1 }, 
            { 10, 32, 1 }, { 15, 32, 1 }, { 27, 32, 1 }, { 1, 32, -1 }, { 15, 32, -1 }, { 22, 32, -1 }, { 25, 32, -1 },
            { 1, 33, 1 }, { 10, 33, 1 }, { 16, 33, 1 }, { 25, 33, 1 }, { 1, 33, -1 }, { 16, 33, -1 }, { 22, 33, -1 }, 
            { 25, 33, -1 }, { 1, 34, 1 }, { 10, 34, 1 }, { 16, 34, 1 }, { 26, 34, 1 }, { 1, 34, -1 }, { 16, 34, -1 }, 
            { 22, 34, -1 }, { 25, 34, -1 }, { 1, 35, 1 }, { 10, 35, 1 }, { 16, 35, 1 }, { 27, 35, 1 }, { 1, 35, -1 }, 
            { 16, 35, -1 }, { 22, 35, -1 }, { 25, 35, -1 }, { 2, 36, 1 }, { 11, 36, 1 }, { 12, 36, 1 }, { 25, 36, 1 }, 
            { 2, 36, -1 }, { 12, 36, -1 }, { 21, 36, -1 }, { 25, 36, -1 }, { 2, 37, 1 }, { 11, 37, 1 }, { 12, 37, 1 }, 
            { 26, 37, 1 }, { 2, 37, -1 }, { 12, 37, -1 }, { 21, 37, -1 }, { 25, 37, -1 }, { 2, 38, 1 }, { 11, 38, 1 }, 
            { 12, 38, 1 }, { 27, 38, 1 }, { 2, 38, -1 }, { 12, 38, -1 }, { 21, 38, -1 }, { 25, 38, -1 }, { 3, 39, 1 }, 
            { 11, 39, 1 }, { 12, 39, 1 }, { 25, 39, 1 }, { 3, 39, -1 }, { 12, 39, -1 }, { 21, 39, -1 }, { 25, 39, -1 }, 
            { 3, 40, 1 }, { 11, 40, 1 }, { 12, 40, 1 }, { 26, 40, 1 }, { 3, 40, -1 }, { 12, 40, -1 }, { 21, 40, -1 }, 
            { 25, 40, -1 }, { 3, 41, 1 }, { 11, 41, 1 }, { 12, 41, 1 }, { 27, 41, 1 }, { 3, 41, -1 }, { 12, 41, -1 }, 
            { 21, 41, -1 }, { 25, 41, -1 }, { 4, 42, 1 }, { 11, 42, 1 }, { 12, 42, 1 }, { 25, 42, 1 }, { 4, 42, -1 }, 
            { 12, 42, -1 }, { 21, 42, -1 }, { 25, 42, -1 }, { 4, 43, 1 }, { 11, 43, 1 }, { 12, 43, 1 }, { 26, 43, 1 }, 
            { 4, 43, -1 }, { 12, 43, -1 }, { 21, 43, -1 }, { 25, 43, -1 }, { 4, 44, 1 }, { 11, 44, 1 }, { 12, 44, 1 }, 
            { 27, 44, 1 }, { 4, 44, -1 }, { 12, 44, -1 }, { 21, 44, -1 }, { 25, 44, -1 }, { 5, 45, 1 }, { 11, 45, 1 }, 
            { 12, 45, 1 }, { 25, 45, 1 }, { 5, 45, -1 }, { 12, 45, -1 }, { 21, 45, -1 }, { 25, 45, -1 }, { 5, 46, 1 }, 
            { 11, 46, 1 }, { 12, 46, 1 }, { 26, 46, 1 }, { 5, 46, -1 }, { 12, 46, -1 }, { 21, 46, -1 }, { 25, 46, -1 }, 
            { 5, 47, 1 }, { 11, 47, 1 }, { 12, 47, 1 }, { 27, 47, 1 }, { 5, 47, -1 }, { 12, 47, -1 }, { 21, 47, -1 }, 
            { 25, 47, -1 }, { 2, 48, 1 }, { 6, 48, 1 }, { 9, 48, 1 }, { 10, 48, 1 }, { 1, 48, -1 }, { 7, 48, -1 }, 
            { 18, 48, -1 }, { 3, 49, 1 }, { 6, 49, 1 }, { 9, 49, 1 }, { 10, 49, 1 }, { 2, 49, -1 }, { 7, 49, -1 }, 
            { 18, 49, -1 }, { 4, 50, 1 }, { 6, 50, 1 }, { 9, 50, 1 }, { 10, 50, 1 }, { 3, 50, -1 }, { 7, 50, -1 }, 
            { 18, 50, -1 }, { 5, 51, 1 }, { 6, 51, 1 }, { 9, 51, 1 }, { 10, 51, 1 }, { 4, 51, -1 }, { 7, 51, -1 }, 
            { 18, 51, -1 }
        };
        for (int i=0; i<flows.length; i++)
            fg.addIncidence(flows[i][0], flows[i][1], flows[i][2]);
        return fg;
    }
    
    public static void main(String[] args) throws InterruptedException {
        FlowsGenerator fg = initBridgeAndVehicles_V04_P05_N02_unf();
        StructuralAlgorithm.ProgressObserver obs = new StructuralAlgorithm.ProgressObserver() {
            @Override
            public void advance(int step, int total, int s, int t) {
            }
        };
        fg.compute(true, obs);
    }
}
