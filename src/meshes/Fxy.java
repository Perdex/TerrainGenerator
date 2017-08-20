package meshes;

import java.util.ArrayList;
import java.util.Random;
import org.joml.Vector3f;
import org.joml.Vector3d;

public class Fxy{

    public static Mesh asdf(){
        return build(25, 0.1f, null, true, (float x, float y)
                -> 5 * x * y / (Math.abs(x) + Math.abs(y)) / (Math.abs(x) + Math.abs(y) + 1)
        );
    }

    public static Mesh asdf2(){
        return build(25, 0.1f, null, true, (float x, float y)
                -> -(float)(Math.cos((x * x + y * y) / 10) / (0.25 + (x * x + y * y) / 30))// + y*y / 30 + x*x / 30
        );
    }
    
    public static Mesh plane(float z, float size, float increment, boolean upfacing, int shaderMode){
        Mesh mesh = build(size, increment, null, upfacing, (float x, float y) -> z);
        mesh.shaderMode = shaderMode;
        return mesh;
    }
    
    public static Mesh terrainPerlin(){
        
        int size = 50;
        
        seed = System.nanoTime();
        
        float scale = 0.5f;
        return build(size, scale, null, true, (float x, float y) -> {
            float z = 0.2f;
            
            for(int i = 0; i < 5; i++){
                z += Math.pow(0.6, i) * perlinNoise(
                        0.1 * x * (1 << i) + size + i * 52314,
                        0.1 * y * (1 << i) + size + i * 52314);
            }
            
            z = z * 3 + 4 * z * z * z / (1 + z * z * z * z);
            
            return z;
        });
    }
    
    public static Mesh terrainTruePerlin(){
        
        int size = 50;
        
        int[] permutation = makePermutation(512);
        
        float scale = 0.2f;
        return build(size, scale, null, true, (float x, float y) -> {
            float z = 0.2f;
            
            for(int i = 0; i < 5; i++){
                z += Math.pow(0.5, i) * (truePerlin(
                        0.1 * x * (1 << i) + size + i * 52314,
                        0.1 * y * (1 << i) + size + i * 52314,
                        permutation) - 0.5);
            }
            
            z = z * 3 + 4 * z * z * z / (1 + z * z * z * z);
            
            return z;
        });
    }
    
    private static int[] makePermutation(int tablesize){
        int[] permutation = new int[tablesize];
        
        //Make the permutation table
        for(int i = 0; i < tablesize / 2; i++){
            permutation[i] = i;
        }
        Random r = new Random();
        for(int i = tablesize / 2; i > 0; i--){
            int j = r.nextInt(i);
            int temp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = temp;
        }
        
        for(int i = tablesize / 2; i < tablesize; i++){
            permutation[i] = permutation[i % (tablesize / 2)];
        }
        return permutation;
    }
    
    public static double lerp(double a, double b, double x) {
        return a + x * (b - a);
    }
    public static double fade(double t) {
        // Fade function as defined by Ken Perlin.  This eases coordinate values
        // so that they will "ease" towards integral values.  This ends up smoothing
        // the final output.
        return t * t * t * (t * (t * 6 - 15) + 10);			// 6t^5 - 15t^4 + 10t^3
    }
    public static double grad(int hash, double x, double y) {
        int h = hash & 0xff;            // Take the first 4 bits of the hashed value (15 == 0b1111)
        double u = (h&8) == 0 ? x : y;  // If the most significant bit (MSB) of the hash is 0 then set u = x.  Otherwise y.

        double v = (h&4) == 0 ? y : x;

        return ((h&1) == 0 ? u : -u)+((h&2) == 0 ? v : -v); // Use the last 2 bits to decide if u and v are positive or negative.  Then return their addition.
    }
    public static double truePerlin(double x, double y, int[] permutation){

        int xi = (int)Math.floor(x);			// Calculate the "unit cube" that the point asked will be located in
        int yi = (int)Math.floor(y);			// The left bound is ( |_x_|,|_y_|,|_z_| ) and the right bound is that
                                                // plus 1.  Next we calculate the location (from 0.0 to 1.0) in that cube.
        double xf = x-xi;			// We also fade the location to smooth the result.
        double yf = y-yi;

        xi &= permutation.length / 2 - 1;
        yi &= permutation.length / 2 - 1;

        double u = fade(xf);
        double v = fade(yf);

        int aa = permutation[permutation[  xi]+   yi];
        int ab = permutation[permutation[  xi]+(1+yi)];
        int ba = permutation[permutation[1+xi]+   yi];
        int bb = permutation[permutation[1+xi]+(1+yi)];

        double x1, x2;
        x1 = lerp(grad(aa, xf, yf),	// The gradient function calculates the dot product between a pseudorandom
                  grad(ba, xf-1, yf),	// gradient vector and the vector from the input coordinate to the 8
                  u);			// surrounding points in its unit cube.
        x2 = lerp(grad(ab, xf, yf-1),	// This is all then lerped together as a sort of weighted average
                  grad(bb, xf-1, yf-1),// based on the faded (u,v,w) values we made earlier.
                  u);

        return (lerp(x1, x2, v) + 1) / 2;
    }
    
    private static long seed;
    private static Vector3d getRandom(int x, int y){
        Random r = new Random(seed + x + y * 984562356l);
        return new Vector3d(r.nextDouble() - 0.5, r.nextDouble() - 0.5, 1).normalize();
    }
    
    private static double getRandomD(int x, int y){
        Random r = new Random(seed + x * 98 + y);
        return r.nextGaussian() / 5;
    }
    
    private static double perlinNoise(double x, double y){
            int x2 = (int)x;
            int y2 = (int)y;
            
            Vector3d v1 = getRandom(x2, y2);
            Vector3d v2 = getRandom(x2 + 1, y2);
            Vector3d v3 = getRandom(x2, y2 + 1);
            Vector3d v4 = getRandom(x2 + 1, y2 + 1);
            
            double xRem = x % 1;
            double yRem = y % 1;
            
            double d1 = v1.dot(new Vector3d(xRem, yRem, getRandomD(x2, y2)));
            double d2 = v2.dot(new Vector3d(1 - xRem, yRem, getRandomD(x2 + 1, y2)));
            double d3 = v3.dot(new Vector3d(xRem, 1 - yRem, getRandomD(x2, y2 + 1)));
            double d4 = v4.dot(new Vector3d(1 - xRem, 1 - yRem, getRandomD(x2 + 1, y2 + 1)));
            
            double sumx1 = (1 - xRem) * d1 + xRem * d2;
            double sumx2 = (1 - xRem) * d3 + xRem * d4;
            
            double sum = (1 - yRem) * sumx1 + yRem * sumx2;
            
            return sum;
    }

    public static Mesh terrainFractal(){

        Random r = new Random();
        
        int size = 200;
        
        final float[][] map = new float[size][size];

        int iterations = 6;
        for(int i = 0; i < iterations; i++)
            for(int j = 0; j < iterations; j++)
                diamondStep((int)((i + 0.5) * size / iterations), (int)((j + 0.5) * size / iterations), r.nextInt(size), map, r);
        
//        for(boolean b[]: visited){
//            for(boolean b2: b)
//                if(b2)
//                    System.out.print(".");
//                else
//                    System.out.print("O");
//            System.out.println();
//        }

        float scale = 50f / size;
        return build(scale * (size - 1), scale, null, true, (float x, float y) ->
                map[(int)Math.min((x / scale) + size / 2, size - 1)][(int)Math.min((y / scale) + size / 2, size - 1)]);

    }

    private static void diamondStep(int x, int y, int radius, float[][] map, Random r){
        
        double scale = 0.015;
        
        double dz = (r.nextDouble() - 0.49) * scale * radius;
        
        //map[midx][midy] += dz;
        for(int i = 0; i < radius; i++){
            for(int j = 0; i + j < radius; j++){
                //for each, check if in bounds and remove duplicates
                if(i != 0 && j != 0)
                    addToMap(x - i, y - j, dz * (1d - ((double)Math.abs(i) + Math.abs(j)) / radius), map);
                
                if(j != 0)
                    addToMap(x + i, y - j, dz * (1d - ((double)Math.abs(i) + Math.abs(j)) / radius), map);
                
                if(i != 0)
                    addToMap(x - i, y + j, dz * (1d - ((double)Math.abs(i) + Math.abs(j)) / radius), map);
                
                addToMap(x + i, y + j, dz * (1d - ((double)Math.abs(i) + Math.abs(j)) / radius), map);
            }
        }
        
        if(radius < 2)
            return;
        
        int subIterations = 2;
        for(int i = 0; i < subIterations; i++)
            diamondStep(x + r.nextInt(radius) - radius / 2, y + r.nextInt(radius) - radius / 2, radius * 3 / 4, map, r);
            
        
    }
    
    private static void addToMap(int x, int y, double dz, float[][] map){
        if(x >= 0 && x < map.length && y >= 0 && y < map.length)
            map[x][y] += dz;
    }
    
    
    public static Mesh terrainSine(){

        final int octaves = 5, parts = 5;
        final Random r = new Random();
        final double[][] vals = new double[parts][4];

        for(int i = 0; i < parts; i++){
            vals[i][0] = r.nextDouble() * 0.1;
            vals[i][1] = r.nextDouble() * 0.1;
            vals[i][2] = r.nextDouble() * 6;
            vals[i][3] = r.nextDouble() * 6;
            
        }

        return build(50, 0.3f, null, true, (float x, float y) -> {

            double scale = 2;
            float z = 0;
            for(int j = 0; j < octaves; j++){

                for(int i = 1; i < parts; i++){
                    z += scale * Math.cos(x * i * vals[i][0] * Math.pow(2, j) + vals[i][2]) * Math.pow(0.6, j) / parts;
                    z += scale * Math.cos(y * i * vals[i][1] * Math.pow(2, j) + vals[i][3]) * Math.pow(0.6, j) / parts;
                }
            }

            return z;
        });
    }

    public static Mesh build(float range, float inc, Vector3f offset, boolean up, Mesh.f f){
        ArrayList<Float> vert = new ArrayList();
        ArrayList<Float> text = new ArrayList();
        ArrayList<Integer> ind = new ArrayList();
        ArrayList<Float> nor = new ArrayList();
        
        if(offset == null)
            offset = new Vector3f();

        int n = (int) (range / inc) + 1;

        int id = 0;

        for(float i = 0; i < n; i++){

            for(int j = 0; j < n; j++){

                float x = i * inc - range / 2 + offset.x();
                float y = j * inc - range / 2 + offset.y();
                float z = f.z(x, y) + offset.z();

                //coordinates
                vert.add(x);
                vert.add(y);
                vert.add(z);
                
                Vector3f n1 = new Vector3f(1, 0, (float)(f.z(x+inc, y) - f.z(x-inc, y)));
                Vector3f n2 = new Vector3f(0, 1, (float)(f.z(x, y+inc) - f.z(x, y-inc)));

                Vector3f normal = n1.cross(n2).normalize();
                
                if(!up)
                    normal.negate(normal);
                
                nor.add(normal.x());
                nor.add(normal.y());
                nor.add(normal.z());
                
                //textures
                float texturescale = 0.04f;
                float textx = i * texturescale;
                float texty = j * texturescale;
                text.add(textx);
                text.add(texty);

                // make triangles
                if(i > 0 && j > 0){
                    if(!up){
                        ind.add(id);
                        ind.add(id - n - 1);
                        ind.add(id - 1);
                        
                        ind.add(id);
                        ind.add(id - n);
                        ind.add(id - n - 1);
                    }else{
                        ind.add(id);
                        ind.add(id - 1);
                        ind.add(id - n - 1);
                        
                        ind.add(id);
                        ind.add(id - n - 1);
                        ind.add(id - n);
                    }
                    
                }

                id++;
            }
        }
        Mesh m = new Mesh(vert, text, ind, nor);
        return m;
    }
}
