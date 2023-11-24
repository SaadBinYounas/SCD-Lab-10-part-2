import java.io.*;
import java.util.*;
class User {
    static int userCounter = 1;
    private static int userID;
    private String userName;
    private String password;
    public User(String userName, String password) {
        userID=userCounter++;
        this.userName = userName;
        this.password = password;
    }
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    @Override
    public String toString() {
        return userID + "," + userName + "," + password;
    }
    static void login(List<User> users, Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.next();
        System.out.print("Enter your password: ");
        String password = scanner.next();
        for (User user : users) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                System.out.println("Login successful!");
                int userChoice;
                do {
                    System.out.println("------------------");
                    System.out.println("User Menu:");
                    System.out.println("1. Create Post");
                    System.out.println("2. Comment on a Post");
                    System.out.println("3. Delete Post");
                    System.out.println("4. Logout");
                    System.out.print("Enter your choice: ");
                    userChoice = scanner.nextInt();
                    switch (userChoice) {
                        case 1:
                            System.out.print("Enter post caption: ");
                            scanner.nextLine();
                            String caption = scanner.nextLine();
                            Post.createPost(username,caption);
                            System.out.println("Post created successfully!");
                            break;
                        case 2:
                            System.out.print("Enter the Post ID to comment on: ");
                            int postID = scanner.nextInt();
                            if (Post.getPostID() == postID) {
                                System.out.print("Enter your name: ");
                                String authorName = scanner.next();
                                System.out.print("Enter your comment: ");
                                scanner.nextLine();
                                String text = scanner.nextLine();
                                Comment.commentOnPost(postID,text,authorName);
                                break;
                            }
                            break;
                        case 3:
                            System.out.print("Enter the Post ID to delete: ");
                            int postIDToDelete = scanner.nextInt();
                            if (Post.getPostID() == postIDToDelete) {
                                Post.deletePost(postIDToDelete);
                                break;
                            }
                            break;
                        case 4:
                            System.out.println("Logging out.");
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } while (userChoice != 4);
            }
        }
        System.out.println("Invalid username or password.");
    }
    static void signUp(List<User> users, Scanner scanner) {
        System.out.print("Enter a new username: ");
        String username = scanner.next();
        System.out.print("Enter a password: ");
        String password = scanner.next();
        for (User user : users) {
            if (user.getUserName().equals(username)) {
                System.out.println("Username already taken. Please choose another one.");
                return;
            }
        }
        User newUser = new User(username, password);
        users.add(newUser);
        Main.saveUsersToFile(users);
        System.out.println("Sign-up successful!");
    }
}
class Post {
    private static int postCounter = 1;
    private static int postID;
    private static String caption;
    private String authorname;
    static List<Post> posts = new ArrayList<>();
    public Post(String caption, String authorname) {
        this.postID = postCounter++;
        this.caption = caption;
        this.authorname=authorname;
    }
    public Post(int postID,String caption, String authorname) {
        this.postID = postID;
        this.caption = caption;
        this.authorname=authorname;
    }
    public static int getPostID() {
        return postID;
    }
    @Override
    public String toString() {
        return postID + "," + authorname + "," + caption;
    }
    public static void createPost(String authorName, String caption) {
        Post newPost = new Post(caption, authorName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("posts.txt", true))) {
            writer.write(newPost.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void deletePost(int postID) {
        List<Post> posts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("posts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] postData = line.split(",");
                if (postData.length >= 3) {
                    int id = Integer.parseInt(postData[0]);
                    String authorName = postData[1];
                    String caption = postData[2];
                    Post post = new Post(id,caption, authorName);
                    posts.add(post);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Post postToDelete = null;
        for (Post post : posts) {
            if (post.getPostID() == postID) {
                postToDelete = post;
                break;
            }
        }
        if (postToDelete != null) {
            posts.remove(postToDelete);
            System.out.println("Post deleted successfully!");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("posts.txt", false))) {
                for (Post post : posts) {
                    writer.write(post.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Post not found. Please enter a valid Post ID.");
        }
    }
}
class Comment {
    private static int commentCounter = 1;
    private int commentID;
    private int postID;
    private String authorName;
    private String text;
    public Comment(int postID,String text,String authorName) {
        this.commentID = commentCounter++;
        this.text = text;
        this.postID=postID;
        this.authorName=authorName;
    }
    public int getCommentID() {
        return commentID;
    }
    public String getText() {
        return text;
    }
    @Override
    public String toString() {
        return commentID +","+postID+ "," + text+","+authorName;
    }
    static void commentOnPost(int postID,String text, String authorName){
        Comment comment = new Comment(postID, text,authorName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("comments.txt", true))) {
            writer.write(comment.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Comment added successfully!");
    }
}
public class Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        loadUsersFromFile(users);
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("------------------");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    User.login(users, scanner);
                    break;
                case 2:
                    User.signUp(users, scanner);
                    break;
                case 3:
                    System.out.println("Exiting the application.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
        saveUsersToFile(users);
    }
    private static void loadUsersFromFile(List<User> users) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            System.out.println("Reading data from account users files:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] userData = line.split(",");
                if (userData.length == 3) {
                    int userID = Integer.parseInt(userData[0]);
                    //System.out.println(userID);
                    String userName = userData[1];
                    //System.out.println(userName);
                    String password = userData[2];
                    //System.out.println(password);
                    users.add(new User(userName, password));
                    User.userCounter = userID + 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void saveUsersToFile(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
