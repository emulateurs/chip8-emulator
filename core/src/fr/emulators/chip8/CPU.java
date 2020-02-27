package fr.emulators.chip8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.Arrays;

public class CPU {

    public int stackPointer = 0;
    private final int STARTADRESS = 512;
    public int programCounter = STARTADRESS;
    private int[] stack = new int[16];
    public int systemCounter = 60;
    public int soundCounter = 60;
    private Components components;
    private int jumpNumber = 0;

    public CPU(Components components){
        Arrays.fill(stack, (byte)0);
        this.components = components;
    }

    public void execute() throws InterruptedException {
        int opcode = getOpCode();
        int action = components.getAction(opcode);

        int b1, b2, b3;

        b3=(opcode&(0x0F00))>>8;  //on prend les 4 bits, b3 représente X
        b2=(opcode&(0x00F0))>>4;  //idem, b2 représente Y
        b1=(opcode&(0x000F));     //on prend les 4 bits de poids faible

        switch(action){

        case 0:
            //Cet opcode n'est pas implémenté.
            break;

        case 1: //00E0 efface l'écran.
            components.initPixels();
            break;


        case 2: //00EE revient du saut.
            if(jumpNumber > 0){
                jumpNumber--;
                programCounter = stack[jumpNumber];

            }
            break;

        case 3: //1NNN effectue un saut à l'adresse 1NNN.
            programCounter=(b3<<8)+(b2<<4)+b1;
            programCounter-=2;
            break;

        case 4:
            //2NNN appelle le sous-programme en NNN, mais on revient ensuite.
            stack[jumpNumber] = programCounter;

            if(jumpNumber < 15){
                jumpNumber++;
            }

            programCounter=(b3<<8)+(b2<<4)+b1;
            programCounter-=2;

            break;

        case 5://3XNN saute l'instruction suivante si VX est égal à NN.
            if(components.registres[b3] == ((b2<<4)+b1)){
                programCounter += 2;
            }

            break;

        case 6://4XNN saute l'instruction suivante si VX et NN ne sont pas égaux.
            if(components.registres[b3] != ((b2<<4)+b1)){
                programCounter += 2;
            }

            break;

        case 7:
            //5XY0 saute l'instruction suivante si VX et VY sont égaux.
            if(components.registres[b3] == components.registres[b2]){
                programCounter += 2;
            }

            break;


        case 8:
            //6XNN définit VX à NN.
            components.registres[b3] = (byte)((b2<<4)+b1);

            break;

        case 9:
            //7XNN ajoute NN à VX.
            components.registres[b3] += (byte)((b2<<4)+b1);

            break;

        case 10:
            //8XY0 définit VX à la valeur de VY.
            components.registres[b3] = components.registres[b2];

            break;

        case 11:
            //8XY1 définit VX à VX OR VY.
            components.registres[b3] = (byte)(components.registres[b3]|components.registres[b2]);

            break;

        case 12:
            //8XY2 définit VX à VX AND VY.
            components.registres[b3] = (byte)(components.registres[b3]&components.registres[b2]);

            break;

        case 13:
            //8XY3 définit VX à VX XOR VY.
            components.registres[b3] = (byte)(components.registres[b3]^components.registres[b2]);


            break;

        case 14:
            //8XY4 ajoute VY à VX. VF est mis à 1 quand il y a un dépassement de mémoire (carry), et à 0 quand il n'y en pas.

            if(components.registres[b3]+components.registres[b2] >= 0xFE) components.registres[0xF] = 1;
            else components.registres[0xF] = 0;
            components.registres[b3] += components.registres[b2];

            break;

        case 15:
            //8XY5 VY est soustraite de VX. VF est mis à 0 quand il y a un emprunt, et à 1 quand il n'y a en pas.
            if(components.registres[b2] > components.registres[b3]) components.registres[0xF] = 1;
            else components.registres[0xF] = 0;

            components.registres[b3] -= components.registres[b2];

            break;

        case 16:

            //8XY6 décale (shift) VX à droite de 1 bit. VF est fixé à la valeur du bit de poids faible de VX avant le décalage.
            components.registres[0xF] = (byte)(components.registres[b3]&0x000F);
            components.registres[b3] = (byte)(components.registres[b3]>>1);

            break;

        case 17:
            //8XY7 VX = VY - VX. VF est mis à 0 quand il y a un emprunt et à 1 quand il n'y en a pas.

            if(components.registres[b2] < components.registres[b3]) components.registres[0xF] = 0;
            else components.registres[0xF] = 1;

            components.registres[b3] = (byte)(components.registres[b2] - components.registres[b3]);
            break;

        case 18:
            //8XYE décale (shift) VX à gauche de 1 bit. VF est fixé à la valeur du bit de poids fort de VX avant le décalage.
            components.registres[0xF] = (byte)((components.registres[b3]&0xF000)>>3);
            components.registres[b3] = (byte)(components.registres[b3]<<1);

            break;


        case 19:

            //9XY0 saute l'instruction suivante si VX et VY ne sont pas égaux.
            if(components.registres[b3] != components.registres[b2]) programCounter+=2;

            break;

        case 20:
            //ANNN affecte NNN à I.
            stackPointer = (b3<<8)+(b2<<4)+b1;

            break;

        case 21:
            //BNNN passe à l'adresse NNN + V0.
            programCounter = (b3<<8)+(b2<<4)+b1+components.registres[0];

            break;


        case 22:

            //CXNN définit VX à un nombre aléatoire inférieur à NN.
            components.registres[b3] = (byte)(Math.random()*((b2<<4)+b1+1));

            break;


        case 23:
            //DXYN dessine un sprite aux coordonnées (VX, VY).

            components.dessinerEcran(b1, b2, b3);

            break;


        case 24:
            //EX9E saute l'instruction suivante si la clé stockée dans VX est pressée.
            if(Gdx.input.isKeyJustPressed(components.registres[b3])) programCounter += 2;

            break;

        case 25:
            //EXA1 saute l'instruction suivante si la clé stockée dans VX n'est pas pressée.
            if(!Gdx.input.isKeyJustPressed(components.registres[b3])) programCounter += 2;

            break;


        case 26:
            //FX07 définit VX à la valeur de la temporisation.


            break;

        case 27:
            //FX0A attend l'appui sur une touche et la stocke ensuite dans VX.
            int keyPressed;
            do{
                keyPressed = getKey();
                Thread.sleep(1);
            }while(keyPressed == -1);
            components.registres[b3] = (byte)keyPressed;

            break;


        case 28:
            //FX15 définit la temporisation à VX.
            systemCounter = components.registres[b3];


            break;

        case 29:
            //FX18 définit la minuterie sonore à VX.
            soundCounter = components.registres[b3];

            break;

        case 30:
            //FX1E ajoute à VX I. VF est mis à 1 quand il y a overflow (I+VX>0xFFF), et à 0 si tel n'est pas le cas.

            if(stackPointer+components.registres[b3] > 0xFFF) components.registres[0xF] = 1;
            else components.registres[0xF] = 0;

            components.registres[b3] += stackPointer;

            break;


        case 31:
            //FX29 définit I à l'emplacement du caractère stocké dans VX. Les caractères 0-F (en hexadécimal) sont représentés par une police 4x5.
            stackPointer = 5*components.registres[b3];

            break;


        case 32:
            //FX33 stocke dans la mémoire le code décimal représentant VX (dans I, I+1, I+2).
            components.ram.set(stackPointer, (byte)((components.registres[b3]-components.registres[b3]%100)/100));
            components.ram.set(stackPointer+1, (byte)(((components.registres[b3]-components.registres[b3]%10)/10)%10));
            components.ram.set(stackPointer+2, (byte)(components.registres[b3]-components.ram.get(stackPointer)*100-components.ram.get(stackPointer+1)*10));

            break;

        case 33:

            //FX55 stocke V0 à VX en mémoire à partir de l'adresse I.
            for(int i = 0; i < components.registres.length; i++){
                components.ram.set(i+stackPointer, components.registres[i]);
            }

            break;

        case 34:
            //FX65 remplit V0 à VX avec les valeurs de la mémoire à partir de l'adresse I.

            for(int i = 0; i < components.ram.size(); i++){
                components.registres[i] = components.ram.get(i+stackPointer);
            }

            break;


        default: //si ça arrive, il y un truc qui cloche
            System.err.println("Incorrect opcode ! (opcode : "+opcode+" and action : "+action+")");
            break;
        }
        programCounter += 2; //on passe au prochain opcode
    }

    public int getKey(){
        for (int i=0;i<256;i++){
            if (Gdx.input.isKeyJustPressed(i)){
                return i;
            }
        }
        return -1;
    }

    public int getOpCode(){
        return (components.ram.get(programCounter)<<8)+components.ram.get(programCounter+1);
    }

}
