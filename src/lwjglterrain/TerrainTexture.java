package lwjglterrain;


public class TerrainTexture{

    private final Texture groundTexture, beachTexture, greenTexture, snowTexture;

    public TerrainTexture(String groundTexture, String beachTexture, String greenTexture, String snowTexture){
        this.groundTexture = new Texture(groundTexture);
        this.beachTexture = new Texture(beachTexture);
        this.greenTexture = new Texture(greenTexture);
        this.snowTexture = new Texture(snowTexture);
    }
    
    public void bind(){
        groundTexture.bind(0);
        beachTexture.bind(1);
        greenTexture.bind(2);
        snowTexture.bind(3);
    }
    
}
