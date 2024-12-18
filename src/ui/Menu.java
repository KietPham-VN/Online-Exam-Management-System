package ui;

import java.util.ArrayList;

import utils.Inputter;

public class Menu {

    // properties
    public ArrayList<String> optionList = new ArrayList<>();
    public String title;

    // constuctor
    public Menu(String title) {
        this.title = title;
    }

    // other method  
    // hàm thêm lựa chọn vào menu
    public void addNewOption(String newOption) {
        optionList.add(newOption);
    }

    // hàm in ra menu
    public void print() {
        int count = 1;
        System.out.println("_______________" + title + "_______________");
        for (String op : optionList) {
            System.out.println(count + ". " + op);
            count++;
        }
    }

    // hàm lấy lựa chọn từ người đùng
    public int getChoice() {
        int choice = Inputter.getAnInteger("Input your choice: ", "That field is required",1,optionList.size());
        return choice;
    }

    // hàm check xem người dùng có muốn tiếp tục không 
    //Add thêm option để thay đổi question
    public static boolean isContinue(String askStr) {
        String continueChoice;
        while (true) {
            continueChoice = Inputter.getString(askStr, "That field is required").trim();
            if (continueChoice.equalsIgnoreCase("y")
                    || continueChoice.equalsIgnoreCase("yes")) {
                return true;
            } else if (continueChoice.equalsIgnoreCase("n")
                    || continueChoice.equalsIgnoreCase("no")) {
                return false;
            } else {
                System.out.println("Please enter y/n or yes/no.");
            }
        }
    }
    
    public static boolean isContinue(){
        return isContinue("Do you wish to continue(y/n)? ");
    }
    
}
