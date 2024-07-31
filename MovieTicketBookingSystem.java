import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MovieTicketBookingSystem extends JFrame {

    private JList<String> movieList;
    private JTextArea bookedTicketsArea;
    private JButton bookTicketButton, viewBookedTicketsButton, cancelBookingButton;
    private Connection connection;

    public MovieTicketBookingSystem() {
        setTitle("Movie Ticket Booking System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        movieList = new JList<>();
        bookedTicketsArea = new JTextArea();
        bookTicketButton = new JButton("Book Ticket");
        viewBookedTicketsButton = new JButton("View Booked Tickets");
        cancelBookingButton = new JButton("Cancel Booking");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(bookTicketButton);
        panel.add(viewBookedTicketsButton);
        panel.add(cancelBookingButton);

        add(new JScrollPane(movieList), BorderLayout.NORTH);
        add(new JScrollPane(bookedTicketsArea), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // Add action listeners to buttons
        bookTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookTicket();
            }
        });

        viewBookedTicketsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewBookedTickets();
            }
        });

        cancelBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelBooking();
            }
        });

        setupDatabase();
        loadMovies();
    }

    private void setupDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:movie_booking.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS movies (id INTEGER PRIMARY KEY, title TEXT, available_seats INTEGER)");
            statement.execute("CREATE TABLE IF NOT EXISTS tickets (id INTEGER PRIMARY KEY, movie_id INTEGER, FOREIGN KEY (movie_id) REFERENCES movies(id))");
            statement.execute("INSERT INTO movies (title, available_seats) VALUES ('Movie 1', 100), ('Movie 2', 50), ('Movie 3', 75)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMovies() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT title, available_seats FROM movies");
            while (resultSet.next()) {
                String movie = resultSet.getString("title") + " - Seats: " + resultSet.getInt("available_seats");
                listModel.addElement(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        movieList.setModel(listModel);
    }

    private void bookTicket() {
        String selectedMovie = movieList.getSelectedValue();
        if (selectedMovie == null) {
            JOptionPane.showMessageDialog(this, "Please select a movie first.");
            return;
        }

        String movieTitle = selectedMovie.split(" - ")[0];
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, available_seats FROM movies WHERE title = ?");
            statement.setString(1, movieTitle);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int movieId = resultSet.getInt("id");
                int availableSeats = resultSet.getInt("available_seats");

                if (availableSeats > 0) {
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE movies SET available_seats = ? WHERE id = ?");
                    updateStatement.setInt(1, availableSeats - 1);
                    updateStatement.setInt(2, movieId);
                    updateStatement.executeUpdate();

                    PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO tickets (movie_id) VALUES (?)");
                    insertStatement.setInt(1, movieId);
                    insertStatement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Ticket booked successfully.");
                    loadMovies();
                } else {
                    JOptionPane.showMessageDialog(this, "No seats available.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewBookedTickets() {
        StringBuilder bookedTickets = new StringBuilder();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT tickets.id, movies.title FROM tickets JOIN movies ON tickets.movie_id = movies.id");

            while (resultSet.next()) {
                bookedTickets.append("Ticket ID: ").append(resultSet.getInt("id")).append(", Movie: ").append(resultSet.getString("title")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bookedTicketsArea.setText(bookedTickets.toString());
    }

    private void cancelBooking() {
        String ticketIdStr = JOptionPane.showInputDialog(this, "Enter Ticket ID to cancel:");
        if (ticketIdStr == null || ticketIdStr.isEmpty()) {
            return;
        }

        int ticketId = Integer.parseInt(ticketIdStr);
        try {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT movie_id FROM tickets WHERE id = ?");
            selectStatement.setInt(1, ticketId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int movieId = resultSet.getInt("movie_id");

                PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM tickets WHERE id = ?");
                deleteStatement.setInt(1, ticketId);
                deleteStatement.executeUpdate();

                PreparedStatement updateStatement = connection.prepareStatement("UPDATE movies SET available_seats = available_seats + 1 WHERE id = ?");
                updateStatement.setInt(1, movieId);
                updateStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
                loadMovies();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Ticket ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MovieTicketBookingSystem().setVisible(true);
            }
        });
    }
}
