package fr.emulators.chip8;

import java.util.Arrays;

public class Memory {

    private byte[] memory;

    public Memory(int size){
        this.memory = new byte[size];

        Arrays.fill(memory, (byte) 0);
    }

    public byte get(int address){
        if(address < 0) {
            System.err.println("Error : trying to access a negative value ("+address+"). 0 returned.");
            return 0;
        }
        return memory[address];
    }

    public void set(int address, byte value){
        this.memory[address] = value;
    }

    public int size() {
        return memory.length;
    }
}
