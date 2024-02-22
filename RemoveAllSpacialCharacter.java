package com.API;

public class RemoveAllSpacialCharacter {

	public static String removeSpacesAndSpecialCharacters(String input) {
        // Remove spaces and special characters using the regex pattern
        String result = input.replaceAll("[^a-zA-Z0-9]", "");

        return result;
    }

    public static void main(String[] args) {
        // Example usage
        String input = "Hello @World!";
        String output = removeSpacesAndSpecialCharacters(input);
        System.out.println(output); // Output: HelloWorld
    }
}
