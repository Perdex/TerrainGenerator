package lwjglterrain;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Camera{

    private static final float maxSpeed = 5;// * LWJGLTerrain.uniformScalingFactor;

    private static Vector3f pos;
    private static Matrix4f proj, proj2D, scale;
    private static Quaternionf rot;

    static int targetMesh = 0;
    private static float movementSpeed = 0.5f;

    public static Camera makeFreeCamera(){
        
        return new Camera(){
            @Override
            public void translate(float amount, int axis){
                translateFree(amount, axis);
            }

            @Override
            public void rotate(float angle, int axis){
                switch(axis){
                    case 0:
                        rot.rotateAxis(angle, rot.positiveX(new Vector3f()));
                        break;
                    case 1:
                        rot.rotateAxis(angle, rot.positiveY(new Vector3f()));
                        break;
                    case 2:
                        rot.rotateAxis(angle, rot.positiveZ(new Vector3f()));
                        break;
                }
            }
        };
    }

    public static Camera makeConstrainedCamera(){
        
        return new Camera(){
            @Override
            public void translate(float amount, int axis){
                amount *= this.getSpeed();

                switch(axis){
                    case 0:
                        pos.add(rot.positiveZ(new Vector3f()).cross(new Vector3f(0, 0, -1)).normalize().mul(amount));
                        break;
                    case 1:
                        pos.add(0, 0, amount);
                        break;
                    case 2:
                        translateFree(-amount / this.getSpeed(), axis);
                        //pos.add(rot.positiveX(new Vector3f()).cross(new Vector3f(0, 0, 1)).normalize().mul(amount));
                        break;
                }
            }

            @Override
            public void rotate(float angle, int axis){
                switch(axis){
                    case 0:
                        rot.rotateAxis(angle, rot.positiveX(new Vector3f()));
                        break;
                    case 1:
                        rot.rotateZ(angle);
                        break;
                }
            }
        };
    }

    private Camera(){
        float dist = 100f;
        pos = new Vector3f(0, -dist, dist / 2);

        proj = new Matrix4f().setPerspective((float) Math.PI / 2.5f, 16f / 9f, 0.1f, 2000);
        proj2D = new Matrix4f().setOrtho2D(-0.5f, 0.5f, -0.5f, 0.5f);

        rot = new Quaternionf(0, 0, 0, 1).rotateX(-1.2f);

        scale = new Matrix4f().scale(5);
    }

    public void translateFree(float amount, int axis){
        amount *= -getSpeed();
        switch(axis){
            case 0:
                pos.add(rot.positiveX(new Vector3f()).mul(amount));
                break;
            case 1:
                pos.add(rot.positiveY(new Vector3f()).mul(amount));
                break;
            case 2:
                pos.add(rot.positiveZ(new Vector3f()).mul(amount));
                break;
        }
    }

    protected float getSpeed(){
        return (float)Math.pow(movementSpeed, 3) * maxSpeed;
    }

    public static float getDrawSpeed(){
        return movementSpeed;
    }

    public static void addSpeed(float dv){
        movementSpeed = Math.max(Math.min(movementSpeed + dv, 1), 0.01f);
    }

    public abstract void translate(float amount, int axis);

    public abstract void rotate(float amount, int axis);


    public Vector3f getNormal(){
        return rot.positiveZ(new Vector3f());
    }
    
    private Vector3f getRelativePosition(){
        return pos.mul((float)Math.pow(LWJGLTerrain.getMesh(targetMesh).getScale().z(), 0.8), new Vector3f());
    }

    public Vector3f getPosition(){
        //return pos.add(new Vector3f(), new Vector3f());
        //System.out.println(pos + " + " + targetMesh.getDrawPos() + " = " + pos.add(targetMesh.getDrawPos(), new Vector3f()));

        return getRelativePosition().add(LWJGLTerrain.getMesh(targetMesh).getPos());
    }

    public Matrix4f getProjection(){
        Matrix4f target = new Matrix4f();
        Matrix4f pos2 = new Matrix4f().setTranslation(getPosition().mul(-1f, new Vector3f()));

        target = proj.rotate(rot, target);
        target = target.mul(pos2, target);
        return target.mul(scale);
    }

    public Matrix4f get2DProjection(){
        return proj2D;
    }

}
