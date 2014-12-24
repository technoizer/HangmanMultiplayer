/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package command;

import java.io.Serializable;

/**
 *
 * @author Muhammad Izzuddin
 */
public class CommandList implements Serializable{
    private String command;
    private String commandDetails;

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return the commandDetails
     */
    public String getCommandDetails() {
        return commandDetails;
    }

    /**
     * @param commandDetails the commandDetails to set
     */
    public void setCommandDetails(String commandDetails) {
        this.commandDetails = commandDetails;
    }
}
