package fr.emulators.chip8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Components {

    public final Memory ram;
    public final byte[] registres = new byte[16];
    private Pixel[][] screen = new Pixel[64][32];
    public final CPU cpu;

    //INSTRUCTIONS
    public final int[] id = new int[35];
    public final int[] masque = new int[35];

    public Components() {
        Arrays.fill(registres, (byte) 0);
        this.ram = new Memory(4096);
        this.cpu = new CPU(this);
        initInstructions();
        loadFont();
    }
    
    public void loadFont(){
        ram.set(0, (byte)0xF0); ram.set(1, (byte) 0x90); ram.set(2, (byte) 0x90); ram.set(3, (byte) 0x90);  ram.set(4, (byte) 0xF0);  // O

        ram.set(5, (byte) 0x20); ram.set(6, (byte) 0x60); ram.set(7, (byte) 0x20); ram.set(8, (byte) 0x20); ram.set(9, (byte) 0x70);  // 1 

        ram.set(10, (byte) 0xF0); ram.set(11, (byte) 0x10); ram.set(12, (byte) 0xF0); ram.set(13, (byte) 0x80);  ram.set(14, (byte) 0xF0);  // 2 

        ram.set(15, (byte) 0xF0); ram.set(16, (byte) 0x10); ram.set(17, (byte) 0xF0); ram.set(18, (byte) 0x10); ram.set(19, (byte) 0xF0);  // 3 

        ram.set(20, (byte) 0x90); ram.set(21, (byte) 0x90); ram.set(22, (byte) 0xF0); ram.set(23, (byte) 0x10); ram.set(24, (byte) 0x10);  // 4 

        ram.set(25, (byte) 0xF0); ram.set(26, (byte) 0x80); ram.set(27, (byte) 0xF0); ram.set(28, (byte) 0x10); ram.set(29, (byte) 0xF0);  // 5 

        ram.set(30, (byte) 0xF0); ram.set(31, (byte) 0x80); ram.set(32, (byte) 0xF0); ram.set(33, (byte) 0x90); ram.set(34, (byte) 0xF0);  // 6 

        ram.set(35, (byte) 0xF0); ram.set(36, (byte) 0x10); ram.set(37, (byte) 0x20); ram.set(38, (byte) 0x40); ram.set(39, (byte) 0x40);  // 7 

        ram.set(40, (byte) 0xF0); ram.set(41, (byte) 0x90); ram.set(42, (byte) 0xF0); ram.set(43, (byte) 0x90); ram.set(44, (byte) 0xF0);  // 8 

        ram.set(45, (byte) 0xF0); ram.set(46, (byte) 0x90); ram.set(47, (byte) 0xF0); ram.set(48, (byte) 0x10); ram.set(49, (byte) 0xF0);  // 9 

        ram.set(50, (byte) 0xF0); ram.set(51, (byte) 0x90); ram.set(52, (byte) 0xF0); ram.set(53, (byte) 0x90); ram.set(54, (byte) 0x90);  // A 

        ram.set(55, (byte) 0xE0); ram.set(56, (byte) 0x90); ram.set(57, (byte) 0xE0); ram.set(58, (byte) 0x90); ram.set(59, (byte) 0xE0);  // B 

        ram.set(60, (byte) 0xF0); ram.set(61, (byte) 0x80); ram.set(62, (byte) 0x80); ram.set(63, (byte) 0x80); ram.set(64, (byte) 0xF0);  // C
    }

    void dessinerEcran(int b1, int b2, int b3) {
        int x = 0, y = 0, k = 0, codage = 0, j = 0, decalage = 0;
        registres[0xF] = 0;

        for (k = 0; k < b1; k++) {
            codage = ram.get(cpu.stackPointer + k);//on récupère le codage de la ligne à dessiner

            y = (registres[b2] + k) % 32;//on calcule l'ordonnée de la ligne à dessiner, on ne doit pas dépasser L

            for (j = 0, decalage = 7; j < 8; j++, decalage--) {
                x = (registres[b3] + j) % 64; //on calcule l'abscisse, on ne doit pas dépasser l

                if (((codage) & (0x1 << decalage)) != 0)//on récupère le bit correspondant
                {   //si c'est blanc
                    if (screen[x][y].color)//le pixel était blanc
                    {
                        screen[x][y].color = false; //on l'éteint
                        registres[0xF] = 1; //il y a donc collusion

                    } else //sinon
                    {
                        screen[x][y].color = true;//on l'allume
                    }


                }

            }
        }

    }

    private void initInstructions() {
        masque[0] = 0x0000;
        id[0] = 0x0FFF;          /* 0NNN */
        masque[1] = 0xFFFF;
        id[1] = 0x00E0;          /* 00E0 */
        masque[2] = 0xFFFF;
        id[2] = 0x00EE;          /* 00EE */
        masque[3] = 0xF000;
        id[3] = 0x1000;          /* 1NNN */
        masque[4] = 0xF000;
        id[4] = 0x2000;          /* 2NNN */
        masque[5] = 0xF000;
        id[5] = 0x3000;          /* 3XNN */
        masque[6] = 0xF000;
        id[6] = 0x4000;          /* 4XNN */
        masque[7] = 0xF00F;
        id[7] = 0x5000;          /* 5XY0 */
        masque[8] = 0xF000;
        id[8] = 0x6000;          /* 6XNN */
        masque[9] = 0xF000;
        id[9] = 0x7000;          /* 7XNN */
        masque[10] = 0xF00F;
        id[10] = 0x8000;          /* 8XY0 */
        masque[11] = 0xF00F;
        id[11] = 0x8001;          /* 8XY1 */
        masque[12] = 0xF00F;
        id[12] = 0x8002;          /* 8XY2 */
        masque[13] = 0xF00F;
        id[13] = 0x8003;          /* BXY3 */
        masque[14] = 0xF00F;
        id[14] = 0x8004;          /* 8XY4 */
        masque[15] = 0xF00F;
        id[15] = 0x8005;          /* 8XY5 */
        masque[16] = 0xF00F;
        id[16] = 0x8006;          /* 8XY6 */
        masque[17] = 0xF00F;
        id[17] = 0x8007;          /* 8XY7 */
        masque[18] = 0xF00F;
        id[18] = 0x800E;          /* 8XYE */
        masque[19] = 0xF00F;
        id[19] = 0x9000;          /* 9XY0 */
        masque[20] = 0xF000;
        id[20] = 0xA000;          /* ANNN */
        masque[21] = 0xF000;
        id[21] = 0xB000;          /* BNNN */
        masque[22] = 0xF000;
        id[22] = 0xC000;          /* CXNN */
        masque[23] = 0xF000;
        id[23] = 0xD000;          /* DXYN */
        masque[24] = 0xF0FF;
        id[24] = 0xE09E;          /* EX9E */
        masque[25] = 0xF0FF;
        id[25] = 0xE0A1;          /* EXA1 */
        masque[26] = 0xF0FF;
        id[26] = 0xF007;          /* FX07 */
        masque[27] = 0xF0FF;
        id[27] = 0xF00A;          /* FX0A */
        masque[28] = 0xF0FF;
        id[28] = 0xF015;          /* FX15 */
        masque[29] = 0xF0FF;
        id[29] = 0xF018;          /* FX18 */
        masque[30] = 0xF0FF;
        id[30] = 0xF01E;          /* FX1E */
        masque[31] = 0xF0FF;
        id[31] = 0xF029;          /* FX29 */
        masque[32] = 0xF0FF;
        id[32] = 0xF033;          /* FX33 */
        masque[33] = 0xF0FF;
        id[33] = 0xF055;          /* FX55 */
        masque[34] = 0xF0FF;
        id[34] = 0xF065;          /* FX65 */
    }

    public int getAction(int opcode) {
        int action;
        int resultat;

        for (action = 0; action < 35; action++) {
            resultat = (masque[action] & opcode);  /* On récupère les bits concernés par le test, l'identifiant de l'opcode */

            if (resultat == id[action]) /* On a trouvé l'action à effectuer */
                break; /* Plus la peine de continuer la boucle car la condition n'est vraie qu'une seule fois*/
        }

        return action;  //on renvoie l'indice de l'action à effectuer
    }

    public void update(ShapeRenderer shapeRenderer) throws InterruptedException {
        drawAllPixels(shapeRenderer);
        cpu.execute();
    }

    public void initPixels() {
        for (int x = 0; x < screen.length; x++) {
            for (int y = 0; y < screen[x].length; y++) {
                screen[x][y] = new Pixel(x, y);
            }
        }
    }

    public void drawAllPixels(ShapeRenderer shapeRenderer) {
        for (Pixel[] pixels : screen) {
            for (Pixel pixel : pixels) {
                pixel.draw(shapeRenderer);
            }
        }
    }

    public void loadGame(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));

        for(int i = 0; i < bytes.length; i++){
            ram.set(i+512, bytes[i]);
            System.out.println("opcode charged in memory : "+bytes[i]);
        }
    }

}
