package ecorayen;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import ecorayen.utils.Myconnection;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Connection connection = Myconnection.getInstance().getConnection();
        System.out.println("Connected to DB: " + connection);

        ServiceChallenge service = new ServiceChallenge();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Challenge Manager =====");
            System.out.println("1. Add a challenge");
            System.out.println("2. View all challenges");
            System.out.println("3. Delete a challenge by ID");
            System.out.println("4. Update a challenge by ID");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // Add challenge logic (same as before)
                    System.out.print("Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Description: ");
                    String description = scanner.nextLine();

                    LocalDate start = null;
                    while (start == null) {
                        System.out.print("Start date (yyyy-mm-dd): ");
                        String startDateStr = scanner.nextLine().trim();
                        if (!startDateStr.isEmpty()) {
                            try {
                                start = LocalDate.parse(startDateStr);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please use yyyy-mm-dd.");
                            }
                        } else {
                            System.out.println("Start date cannot be empty.");
                        }
                    }

                    LocalDate end = null;
                    while (end == null) {
                        System.out.print("End date (yyyy-mm-dd): ");
                        String endDateStr = scanner.nextLine().trim();
                        if (!endDateStr.isEmpty()) {
                            try {
                                end = LocalDate.parse(endDateStr);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please use yyyy-mm-dd.");
                            }
                        } else {
                            System.out.println("End date cannot be empty.");
                        }
                    }

                    System.out.print("Location: ");
                    String location = scanner.nextLine();

                    System.out.print("Image path: ");
                    String image = scanner.nextLine();

                    challenge c = new challenge(name, description, start, end, location, image);
                    service.add(c);
                    System.out.println("✅ Challenge added.");
                    break;

                case "2":
                    // View all challenges logic (same as before)
                    List<challenge> all = service.getAll();
                    if (all.isEmpty()) {
                        System.out.println("No challenges found.");
                    } else {
                        all.forEach(System.out::println);
                    }
                    break;

                case "3":
                    // Delete challenge logic (same as before)
                    System.out.print("Enter challenge ID to delete: ");
                    String idToDeleteStr = scanner.nextLine();
                    if (!idToDeleteStr.trim().isEmpty()) {
                        try {
                            int idToDelete = Integer.parseInt(idToDeleteStr.trim());
                            challenge toDelete = service.getById(idToDelete);
                            if (toDelete != null) {
                                service.delete(toDelete.getId());
                                System.out.println("✅ Challenge deleted.");
                            } else {
                                System.out.println("❌ Challenge not found.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid ID format. Please enter a number.");
                        }
                    } else {
                        System.out.println("Challenge ID cannot be empty.");
                    }
                    break;

                case "4":
                    // Update challenge logic
                    System.out.print("Enter challenge ID to update: ");
                    String idToUpdateStr = scanner.nextLine();
                    if (!idToUpdateStr.trim().isEmpty()) {
                        try {
                            int idToUpdate = Integer.parseInt(idToUpdateStr.trim());
                            challenge existingChallenge = service.getById(idToUpdate);
                            if (existingChallenge != null) {
                                System.out.println("--- Enter new details (leave blank to keep current) ---");

                                System.out.print("New Name (" + existingChallenge.getName() + "): ");
                                String newName = scanner.nextLine();
                                if (newName.isEmpty()) newName = existingChallenge.getName();

                                System.out.print("New Description (" + existingChallenge.getDescription() + "): ");
                                String newDescription = scanner.nextLine();
                                if (newDescription.isEmpty()) newDescription = existingChallenge.getDescription();

                                LocalDate newStart = existingChallenge.getDate_start();
                                System.out.print("New Start date (yyyy-mm-dd) (" + existingChallenge.getDate_start() + "): ");
                                String newStartDateStr = scanner.nextLine().trim();
                                if (!newStartDateStr.isEmpty()) {
                                    try {
                                        newStart = LocalDate.parse(newStartDateStr);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Keeping current start date.");
                                    }
                                }

                                LocalDate newEnd = existingChallenge.getDate_end();
                                System.out.print("New End date (yyyy-mm-dd) (" + existingChallenge.getDate_end() + "): ");
                                String newEndDateStr = scanner.nextLine().trim();
                                if (!newEndDateStr.isEmpty()) {
                                    try {
                                        newEnd = LocalDate.parse(newEndDateStr);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Keeping current end date.");
                                    }
                                }

                                System.out.print("New Location (" + existingChallenge.getLocation() + "): ");
                                String newLocation = scanner.nextLine();
                                if (newLocation.isEmpty()) newLocation = existingChallenge.getLocation();

                                System.out.print("New Image path (" + existingChallenge.getImage() + "): ");
                                String newImage = scanner.nextLine();
                                if (newImage.isEmpty()) newImage = existingChallenge.getImage();

                                challenge updatedChallenge = new challenge(idToUpdate, newName, newDescription, newStart, newEnd, newLocation, newImage);
                                service.update(updatedChallenge);
                                System.out.println("✅ Challenge updated.");

                            } else {
                                System.out.println("❌ Challenge not found.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid ID format. Please enter a number.");
                        }
                    } else {
                        System.out.println("Challenge ID cannot be empty.");
                    }
                    break;

                case "5":
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}