package com.example.softwarepatternsapptobias;

import java.util.ArrayList;
import java.util.List;

public class Invoker {

    private static Invoker instance = new Invoker();

    private List<Command> commandList = new ArrayList<Command>();

    private Invoker() {}

    public static Invoker getInstance(){
        return instance;
    }

    public void takeOrder(Command command){
        commandList.add(command);
    }

    public void placeOrders(){

        for (Command command: commandList) {
            command.execute();
        }
        commandList.clear();
    }
}
