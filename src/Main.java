import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random();

    private Main() {

    }

    public static void main(String[] args) {
        //the width and height of the field must be 2^n+1
        Main.generate(257, 30, 0, 12);
    }

    //only use this method
    public static void generate(int size, float heightDifference, double except, double stdDev) {
        float[][] terrain = new float[size][size];
        initialize(terrain, heightDifference);
        int distance = terrain.length-1;

        for (int i = terrain.length-1;i > 1;i/=2) {
            diamond(terrain, distance, except,stdDev);
            square(terrain, distance, except, stdDev);
            distance/=2;
            stdDev/=2;
        }
        save(terrain, "D:\\IDEAprojects\\Terrain\\Terrain.obj");
    }
    public static void initialize(float[][] terrain, float heightDifference) {
        terrain[0][0] = random.nextFloat()*heightDifference;
        terrain[0][terrain.length-1] = random.nextFloat()*heightDifference;
        terrain[terrain.length-1][0] = random.nextFloat()*heightDifference;
        terrain[terrain.length-1][terrain.length-1] = random.nextFloat()*heightDifference;
    }

    public static float interpolation(double except, double stdDev, List<Float> a) {
        if (a.size() == 0) {
            return 0;
        }
        double displacement = except + random.nextGaussian()*stdDev;
        float sum = 0;
        for (float element: a) {
            sum+=element;
        }
        float average = sum/a.size() ;//average can be improved by considering weights
        return average + (float) displacement;
    }

    public static void diamond(float[][] terrain, int distance, double except, double stdDev) {
        int mid = distance/2;
        for (int i = 0; i < terrain.length-1; i+=distance) {//minus 1 to avoid the situation already considered
            for (int j = 0; j <terrain.length-1; j+=distance) {
                List<Float> list = new ArrayList<>();
                list.add(terrain[i][j]);
                list.add(terrain[i+distance][j]);
                list.add(terrain[i][j+distance]);
                list.add(terrain[i+distance][j+distance]);
                terrain[i+mid][j+mid]= interpolation(except, stdDev, list);
            }
        }
    }

    public static void square(float[][] terrain, int distance, double except, double stdDev) {
        int mid = distance/2;
        for (int i = 0; i < terrain.length; i+=distance) {
            for (int j = 0; j <terrain.length; j+=distance) {
                int x = i+mid;
                if (x<terrain.length) {
                    List<Float> list = new ArrayList<>();
                    list.add(terrain[i][j]);
                    int h = j-mid;
                    if (h>=0) {
                        list.add(terrain[x][h]);
                    }
                    h = j+mid;
                    if (h<terrain.length) {
                        list.add(terrain[x][h]);
                    }
                    int w = i+distance;
                    if (w<terrain.length) {
                        list.add(terrain[w][j]);
                    }
                    terrain[x][j] = interpolation(except, stdDev, list);
                }
                int y = j+mid;
                if (y<terrain.length) {
                    List<Float> list = new ArrayList<>();
                    list.add(terrain[i][j]);
                    int w = i-mid;
                    if (w>=0) {
                        list.add(terrain[w][y]);
                    }
                    w = i+mid;
                    if (w<terrain.length) {
                        list.add(terrain[w][y]);
                    }
                    int h = j+distance;
                    if (h<terrain.length) {
                        list.add(terrain[i][h]);
                    }
                    terrain[i][y] = interpolation(except, stdDev, list);
                }
            }
        }
    }

    public static void save(float[][] terrain, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int j = 0; j < terrain.length; j++) {
                for (int i = 0; i < terrain.length; i++) {
                    int x = i;//left to right
                    int y = -j;//back to forward, minus to fit the programme
                    float z = terrain[i][j];//height
                    writer.write("v " + x + " " + y + " " + z + "\n");
                }
            }
            for (int j = 0; j < terrain.length-1; j++) {
                for (int i = 0; i < terrain.length-1; i++) {
                    //counterclockwise
                    int a = j*terrain.length+i+1;//plus 1 to fit the programme
                    int b = j*terrain.length+i+2;
                    int c = (j+1)*terrain.length+i+1;
                    writer.write("f " + a + " " + c + " " + b +"\n");
                    //counterclockwise
                    a = (j+1)*terrain.length+i+1;
                    b = j*terrain.length+i+2;
                    c = a+1;
                    writer.write("f " + a +" " + c + " " + b + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
