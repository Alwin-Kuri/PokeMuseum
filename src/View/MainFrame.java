/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;
/**
 *
 * @author Kuri
 */
import Controller.CardController;
import Model.PokeCard;
import Model.CardCollection;
import Model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    /**
     * Creates new form MainFrame
     */
    //for login
    private User user;
    private User currentUser;
    private String selectedImagePath = "";
    private boolean isLoggedIn = false;
    private String currentUserRole = "";
    private String currentUsername = "";
    private javax.swing.JList<String> recentList;
    private javax.swing.JScrollPane recentScrollPane;
    private final java.util.Set<PokeCard> selectedCards = new java.util.HashSet<>();
    private final Set<PokeCard> selectedInventoryCards = new HashSet<>(); // tracks selected in inventory
    
    //for card operations
    private CardController controller;
    
    public MainFrame() {
        initComponents();


        //Initialize card controller for sample cards
        controller = new CardController();
        //innitializing user for user data
        user = new User();
        
        //for featured cards in main panel
        populateFeaturedCards();
        populateTopCards();
    }
    
    private void refreshCardTable(List<PokeCard> cardss) {
        if (cardss == null) {
            cardss = new ArrayList<>();
        }
        DefaultTableModel model = (DefaultTableModel) pokeTable.getModel();
        model.setRowCount(0); //clear table to input data

        for (PokeCard card : cardss) {
            model.addRow(new Object[]{
                card.getId(),
                card.getName(),
                card.getType(),
                card.getRarity(),
                card.getCondition(),
                card.getValue()
            });
        }
    }
    
    /**
     * Refreshes the dashboard stats
     */
    public void refreshDashboard(){
        //Total Cards
        totalCardsLabel.setText(String.valueOf(controller.getTotalCards()));

        //Total Inventory Value
        totalValueLabel.setText(String.format("$%.2f", controller.getTotalValue()));

        //Most Valuable Card
        PokeCard mostValuable = controller.getMostValuableCard();
        if (mostValuable != null) {
            valueCardID.setText(mostValuable.getId());
            valueCardName.setText(mostValuable.getName());
            valueCardValue.setText(String.format("%.2f", mostValuable.getValue()));
        } else {
            valueCardID.setText("N/A");
            valueCardName.setText("N/A");
            valueCardValue.setText("$0.00");
        }

        //Most Rare Card
        PokeCard mostRare = controller.getMostRareCard();
        if (mostRare != null) {
            mostRareCardLabel.setText(String.format("%s (%s) - %s", 
                mostRare.getName(), mostRare.getId(), mostRare.getRarity()));
        } else {
            mostRareCardLabel.setText("No cards available");
        }

        //Recent cards
        recentCards.removeAll();

        List<PokeCard> recent = controller.getRecentAdds();

        if (recent.isEmpty()) {
            javax.swing.JLabel empty = new javax.swing.JLabel("No recent cards yet", SwingConstants.CENTER);
            empty.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 14));
            recentCards.add(empty);
        } else {
            for (PokeCard card : recent) {
                javax.swing.JPanel cardEntry = new javax.swing.JPanel();
                cardEntry.setLayout(new java.awt.GridLayout(4, 1, 0, 2));
                cardEntry.setBackground(new java.awt.Color(240, 240, 255)); // light blue tint
                cardEntry.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, java.awt.Color.LIGHT_GRAY));

                javax.swing.JLabel nameLabel = new javax.swing.JLabel("Name: " + card.getName());
                nameLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));

                javax.swing.JLabel idLabel = new javax.swing.JLabel("ID: " + card.getId());
                idLabel.setFont(new java.awt.Font("Segoe UI", 0, 12));

                javax.swing.JLabel valueLabel = new javax.swing.JLabel("Value: $" + String.format("%.2f", card.getValue()));
                valueLabel.setFont(new java.awt.Font("Segoe UI", 0, 12));

                javax.swing.JLabel rarityLabel = new javax.swing.JLabel("Rarity: " + card.getRarity());
                rarityLabel.setFont(new java.awt.Font("Segoe UI", 0, 12));

                cardEntry.add(nameLabel);
                cardEntry.add(idLabel);
                cardEntry.add(valueLabel);
                cardEntry.add(rarityLabel);

                recentCards.add(cardEntry);
            }
        }

        recentCards.revalidate();
        recentCards.repaint();


        recentScroll.getVerticalScrollBar().setValue(0);
    }
    
    private void refreshCollectionView(List<PokeCard> cards) {
        cardsGrid.removeAll();

        if (cards == null || cards.isEmpty()) {
            JLabel msg = new JLabel("No cards found", SwingConstants.CENTER);
            msg.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            cardsGrid.add(msg);
        } else {
            for (PokeCard card : cards) {
                JPanel cardPanel = createBrowseCardPanel(card);
                cardsGrid.add(cardPanel);
            }
        }
        selectedCards.clear();
        updateAddButtonState();
        cardsGrid.revalidate();
        cardsGrid.repaint();
        cardsScroll.getVerticalScrollBar().setValue(0); // scroll to top
    }
    
    private void populateFeaturedCards() {
        featuredGrid.removeAll();

        List<PokeCard> allCards = controller.readAllCards();
        if (allCards.isEmpty()) {
            JLabel emptyLabel = new JLabel("No featured cards available yet", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            featuredGrid.add(emptyLabel);
        } else {
        // Show first 6 (or most valuable / random)
        int maxShow = Math.min(6, allCards.size());
            for (int i = 0; i < maxShow; i++) {
                PokeCard card = allCards.get(i);

                JPanel cardPanel = createFeaturedCardPanel(card);
                featuredGrid.add(cardPanel);
            }
        }

        featuredGrid.revalidate();
        featuredGrid.repaint();
    }
    private void sortInventory() {
        String choice = (String) sortCards1.getSelectedItem();
        List<PokeCard> inventory = new ArrayList<>(currentUser.getInventory());

        switch (choice) {
            case "By Name":
                inventory.sort(Comparator.comparing(PokeCard::getName));
                break;

            case "By Value":
                inventory.sort(Comparator.comparingDouble(PokeCard::getValue));
                break;

            case "By Rarity":
                inventory.sort((c1, c2) -> {
                    int r1 = getRarityRank(c1.getRarity());
                    int r2 = getRarityRank(c2.getRarity());
                    return Integer.compare(r2, r1); // descending order
                });
                break;

            default:
                // "None" → keep original order (as stored in inventory LinkedList)
                break;
        }

        refreshUserInventory(inventory);
    }
    
    private int getRarityRank(String rarity) {
        if (rarity == null) return 0;
        return switch (rarity.toLowerCase()) {
            case "common" -> 1;
            case "uncommon" -> 2;
            case "rare" -> 3;
            case "holo rare", "ultra rare" -> 4;
            case "legendary", "legendary +" -> 5;
            default -> 0;
        };
    }
    
    // Helper: creates one nice card panel
    private JPanel createFeaturedCardPanel(PokeCard card) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(255, 255, 255, 200)); // semi-transparent
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
        panel.setPreferredSize(new Dimension(220, 320));

        // Image
        JLabel imgLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(card.getImagePath()));
            Image scaled = icon.getImage().getScaledInstance(200, 260, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("No Image");
        }
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Info text
        JLabel info = new JLabel(
            "<html><center><b>" + card.getName() + "</b><br>" +
            card.getRarity() + "<br>$" + String.format("%.2f", card.getValue()) + "</center></html>",
            SwingConstants.CENTER
        );
        info.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(imgLabel, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        return panel;
    }
    
    //Top cards for user panel
    private void populateTopCards() {
        topCardGrid.removeAll();

        List<PokeCard> allCards = controller.readAllCards();
        if (allCards.isEmpty()) {
            JLabel emptyLabel = new JLabel("No top cards available yet", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            topCardGrid.add(emptyLabel);
        } else {
        // Show first 6 (or most valuable / random)
        int maxShow = Math.min(6, allCards.size());
            for (int i = 0; i < maxShow; i++) {
                PokeCard card = allCards.get(i);

                JPanel cardPanel = createFeaturedCardPanel(card);
                topCardGrid.add(cardPanel);
            }
        }

        topCardGrid.revalidate();
        topCardGrid.repaint();
    }

    // Helper: creates one nice card panel
    private JPanel createTopCardPanel(PokeCard card) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(255, 255, 255, 200)); // semi-transparent
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
        panel.setPreferredSize(new Dimension(220, 320));

        // Image
        JLabel imgLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(card.getImagePath()));
            Image scaled = icon.getImage().getScaledInstance(200, 260, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("No Image");
        }
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Info text
        JLabel info = new JLabel(
            "<html><center><b>" + card.getName() + "</b><br>" +
            card.getRarity() + "<br>$" + String.format("%.2f", card.getValue()) + "</center></html>",
            SwingConstants.CENTER
        );
        info.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(imgLabel, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        return panel;
    }
    
    //for inventory
    private void refreshUserInventory() {
        inventoryGrid.removeAll();

        if (currentUser == null) {
            inventoryGrid.add(new JLabel("Please login to view your inventory", SwingConstants.CENTER));
        } else {
            List<PokeCard> inv = currentUser.getInventory();

            if (inv.isEmpty()) {
                inventoryGrid.add(new JLabel("Your inventory is empty", SwingConstants.CENTER));
            } else {
                for (PokeCard card : inv) {
                    JPanel cardPanel = createInventoryCardPanel(card);
                    inventoryGrid.add(cardPanel);
                }
            }
        }
        inventoryGrid.revalidate();
        inventoryGrid.repaint();
        updateRemoveButtonState();
        selectedInventoryCards.clear();
        inventoryScroll.getVerticalScrollBar().setValue(0);
    }
    
    private void refreshUserInventory(List<PokeCard> cardsToShow) {
        inventoryGrid.removeAll();

        if (cardsToShow == null || cardsToShow.isEmpty()) {
            inventoryGrid.add(new JLabel("No matching cards found", SwingConstants.CENTER));
        } else {
            for (PokeCard card : cardsToShow) {
                inventoryGrid.add(createInventoryCardPanel(card));
            }
        }

        inventoryGrid.revalidate();
        inventoryGrid.repaint();
        updateRemoveButtonState();
        selectedInventoryCards.clear();
        inventoryScroll.getVerticalScrollBar().setValue(0);
    }
    
    private JPanel createInventoryCardPanel(PokeCard card) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(255, 255, 255, 220));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 1));
        panel.setPreferredSize(new Dimension(220, 340));

        // Make whole panel clickable for toggle selection
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Checkbox (top-right)
        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        checkBox.setVerticalAlignment(SwingConstants.TOP);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                selectedInventoryCards.add(card);
            } else {
                selectedInventoryCards.remove(card);
            }
            updateRemoveButtonState();
        });

        // Image
        JLabel img = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(card.getImagePath()));
            Image scaled = icon.getImage().getScaledInstance(180, 240, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            img.setText("No Image");
        }
        img.setHorizontalAlignment(SwingConstants.CENTER);

        // Overlay checkbox on image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(img, BorderLayout.CENTER);
        imagePanel.add(checkBox, BorderLayout.NORTH);

        // Info
        JLabel info = new JLabel(
            "<html><center><b>" + card.getName() + "</b><br>" +
            card.getRarity() + " - $" + String.format("%.2f", card.getValue()) + "</center></html>",
            SwingConstants.CENTER
        );

        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        // Click whole card to toggle checkbox
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkBox.setSelected(!checkBox.isSelected());
                if (checkBox.isSelected()) {
                    selectedInventoryCards.add(card);
                } else {
                    selectedInventoryCards.remove(card);
                }
                updateRemoveButtonState();
            }
        });

        return panel;
    }
    
    //remove from inventory logic
    private void removeSelectedFromInventory() {
        if (currentUser == null || !"user".equalsIgnoreCase(currentUserRole)) {
            JOptionPane.showMessageDialog(this, "Please login as regular user!", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedInventoryCards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cards selected to remove!", "Nothing Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Confirmation with count
        int count = selectedInventoryCards.size();
        String names = selectedInventoryCards.stream()
            .map(PokeCard::getName)
            .collect(Collectors.joining(", "));
        String msg = "Remove " + count + " selected card(s)?\n\n" +
                     "Cards: " + (names.length() > 100 ? names.substring(0, 100) + "..." : names);

        int confirm = JOptionPane.showConfirmDialog(this,
            msg,
            "Confirm Remove " + count + " Cards",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Perform removal
        int removedCount = 0;
        Set<PokeCard> copy = new HashSet<>(selectedInventoryCards); // avoid concurrent mod
        for (PokeCard card : copy) {
            currentUser.removeFromInventory(card.getId());
            removedCount++;  // assume success (risky but simple)
            selectedInventoryCards.remove(card);
        }

        if (removedCount > 0) {
            JOptionPane.showMessageDialog(this,
                removedCount + " card(s) removed from your inventory!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to remove selected cards (not found).",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

        // Clear selection
        selectedInventoryCards.clear();
        updateRemoveButtonState();

        // Refresh inventory grid
        refreshUserInventory();
    }
    
    private void updateRemoveButtonState() {
        boolean shouldEnable = !selectedInventoryCards.isEmpty() 
                            && currentUser != null 
                            && "user".equalsIgnoreCase(currentUserRole);

        removeSelectedBtn1.setEnabled(shouldEnable);

        // Optional: visual feedback
        removeSelectedBtn1.setText("Remove Selected (" + selectedInventoryCards.size() + ")");
    }
    
    //To add cards into cards collection panel
    private JPanel createBrowseCardPanel(PokeCard card) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(255, 255, 255, 220));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(220, 340));

        // Make whole panel clickable for selection
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Checkbox for selection
        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
        checkBox.setVerticalAlignment(SwingConstants.TOP);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                selectedCards.add(card);
            } else {
                selectedCards.remove(card);
            }
            updateAddButtonState();
        });

        // Image
        JLabel img = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(card.getImagePath()));
            Image scaled = icon.getImage().getScaledInstance(220, 280, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            img.setText("No Image");
        }
        img.setHorizontalAlignment(SwingConstants.CENTER);

        // Info
        JLabel info = new JLabel(
            "<html><center><b>" + card.getName() + "</b><br>" +
            card.getRarity() + "<br>$" + String.format("%.2f", card.getValue()) + "</center></html>",
            SwingConstants.CENTER
        );
        info.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Overlay checkbox on top right of image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(img, BorderLayout.CENTER);
        imagePanel.add(checkBox, BorderLayout.NORTH);

        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(info, BorderLayout.SOUTH);

        // Click whole card to toggle selection
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkBox.setSelected(!checkBox.isSelected());
                if (checkBox.isSelected()) {
                    selectedCards.add(card);
                } else {
                    selectedCards.remove(card);
                }
                updateAddButtonState();
            }
        });

        return panel;
    }
    
    private void updateAddButtonState() {
        addInv.setEnabled(!selectedCards.isEmpty());
    }
    
    private void updateUndoButtonState() {
        if (currentUserRole == null) {
            undoDeleteBtn.setEnabled(false);
            return;
        }

        boolean isAdmin = "admin".equalsIgnoreCase(currentUserRole);
        boolean hasUndo = controller.canUndoDelete();

        boolean canUndo = isAdmin && hasUndo;

        undoDeleteBtn.setEnabled(canUndo);

        // Optional debug
        System.out.println("DEBUG: updateUndoButtonState -> isAdmin: " + isAdmin + 
                       ", hasUndo: " + hasUndo + 
                       ", button enabled: " + canUndo);
    }
    
    private void performUndoDelete() {
        if (!"admin".equalsIgnoreCase(currentUserRole)) {
            JOptionPane.showMessageDialog(this,
                "Only admin can undo delete operations!",
                "Access Denied",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        //Get the card that would be restored
        try {
            PokeCard lastDeleted = controller.peekLastDeletedCard();

            if (lastDeleted == null) {
                JOptionPane.showMessageDialog(this,
                    "No previous delete operation to undo.",
                    "Nothing to Undo",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String message = String.format(
                "Do you want to restore the following card?\n\n" +
                "Name: %s\n" +
                "ID: %s\n" +
                "Rarity: %s\n" +
                "Value: $%.2f\n\n" +
                "This will add it back to the collection.",
                lastDeleted.getName(),
                lastDeleted.getId(),
                lastDeleted.getRarity(),
                lastDeleted.getValue()
            );

            int choice = JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Undo Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                return; // user cancelled
            }

            PokeCard restored = controller.undoDelete();

            //Success message + refresh
            JOptionPane.showMessageDialog(this,
                "Successfully restored:\n" +
                restored.getName() + " (" + restored.getId() + ")",
                "Undo Successful",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh UI
            refreshCardTable(controller.readAllCards());
            refreshDashboard();// updates recent adds
            updateUndoButtonState();// may disable button

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to undo: " + ex.getMessage(),
                "Undo Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        Navbar = new javax.swing.JPanel();
        LogoName = new javax.swing.JLabel();
        CardsNav = new javax.swing.JButton();
        HomeNav = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        SearchNav = new javax.swing.JTextField();
        CollectionNav = new javax.swing.JButton();
        RegisterNav = new javax.swing.JButton();
        LoginNav = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        WelcomePanel = new javax.swing.JPanel();
        WelcomeText = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        featuredScroll = new javax.swing.JScrollPane();
        featuredGrid = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        loginPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        userTF = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        LoginNav1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        LogoName1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dashHomePanel = new javax.swing.JPanel();
        refreshDashboard = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        rareCards = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        mostRareCardLabel = new javax.swing.JLabel();
        totalCards = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        valueCardID = new javax.swing.JLabel();
        valueCardName = new javax.swing.JLabel();
        valueCardValue = new javax.swing.JLabel();
        totalValue = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        totalCardsLabel = new javax.swing.JLabel();
        ValuabelCard = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        totalValueLabel = new javax.swing.JLabel();
        Navbar2 = new javax.swing.JPanel();
        LogoName4 = new javax.swing.JLabel();
        HomeNav2 = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        SearchNav2 = new javax.swing.JTextField();
        CollectionNav2 = new javax.swing.JButton();
        logoutNav = new javax.swing.JButton();
        recentScroll = new javax.swing.JScrollPane();
        recentCards = new javax.swing.JPanel();
        WelcomePanel1 = new javax.swing.JPanel();
        WelcomeText1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        registerPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        registerUN = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        registerPW = new javax.swing.JPasswordField();
        LoginNav2 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        registerCPW = new javax.swing.JPasswordField();
        jLabel14 = new javax.swing.JLabel();
        LogoName2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        collectionPanel = new javax.swing.JPanel();
        cgPanel = new javax.swing.JPanel();
        Navbar1 = new javax.swing.JPanel();
        LogoName3 = new javax.swing.JLabel();
        HomeNav1 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        searchTF = new javax.swing.JTextField();
        CollectionNav1 = new javax.swing.JButton();
        logoutNav1 = new javax.swing.JButton();
        searchBtn = new javax.swing.JButton();
        collectiongrid = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        addNewCard = new javax.swing.JButton();
        editCard = new javax.swing.JButton();
        deleteCard = new javax.swing.JButton();
        undoDeleteBtn = new javax.swing.JButton();
        sortingCB = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        pokeTable = new javax.swing.JTable();
        addCardPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        idTF = new javax.swing.JTextField();
        nameTF = new javax.swing.JTextField();
        typeCB = new javax.swing.JComboBox<>();
        rarityCB = new javax.swing.JComboBox<>();
        conditionCB = new javax.swing.JComboBox<>();
        valueTF = new javax.swing.JTextField();
        cancelAddBtn = new javax.swing.JButton();
        saveCardBtn = new javax.swing.JButton();
        btnChooseImage = new javax.swing.JButton();
        jLabel54 = new javax.swing.JLabel();
        lblImagePreview = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        editCardPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        idTF1 = new javax.swing.JTextField();
        nameTF1 = new javax.swing.JTextField();
        typeCB1 = new javax.swing.JComboBox<>();
        rarityCB1 = new javax.swing.JComboBox<>();
        conditionCB1 = new javax.swing.JComboBox<>();
        valueTF1 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        cancelAddBtn1 = new javax.swing.JButton();
        saveCardBtn1 = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        userPanel = new javax.swing.JPanel();
        WelcomeText2 = new javax.swing.JLabel();
        WelcomeText3 = new javax.swing.JLabel();
        Navbar3 = new javax.swing.JPanel();
        LogoName5 = new javax.swing.JLabel();
        CardsNav3 = new javax.swing.JButton();
        HomeNav3 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        SearchNav1 = new javax.swing.JTextField();
        CollectionNav3 = new javax.swing.JButton();
        LoginNav3 = new javax.swing.JButton();
        seeAllBtn = new javax.swing.JButton();
        topScroll = new javax.swing.JScrollPane();
        topCardGrid = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        userCards = new javax.swing.JPanel();
        cardsShowPanel = new javax.swing.JPanel();
        cardsScroll = new javax.swing.JScrollPane();
        cardsGrid = new javax.swing.JPanel();
        addInv = new javax.swing.JButton();
        sortCardsCB = new javax.swing.JComboBox<>();
        Navbar4 = new javax.swing.JPanel();
        LogoName6 = new javax.swing.JLabel();
        CardsNav4 = new javax.swing.JButton();
        HomeNav4 = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        searchTFcards = new javax.swing.JTextField();
        CollectionNav4 = new javax.swing.JButton();
        LoginNav4 = new javax.swing.JButton();
        searchBtncards2 = new javax.swing.JButton();
        jLabel47 = new javax.swing.JLabel();
        userCollection = new javax.swing.JPanel();
        Navbar5 = new javax.swing.JPanel();
        LogoName7 = new javax.swing.JLabel();
        CardsNav5 = new javax.swing.JButton();
        HomeNav5 = new javax.swing.JButton();
        jLabel50 = new javax.swing.JLabel();
        invSearchTF = new javax.swing.JTextField();
        CollectionNav5 = new javax.swing.JButton();
        LoginNav5 = new javax.swing.JButton();
        searchBtncards1 = new javax.swing.JButton();
        inventoryShow = new javax.swing.JPanel();
        inventoryScroll = new javax.swing.JScrollPane();
        inventoryGrid = new javax.swing.JPanel();
        removeSelectedBtn1 = new javax.swing.JButton();
        sortCards1 = new javax.swing.JComboBox<>();
        jLabel48 = new javax.swing.JLabel();
        removeSelectedBtn = new javax.swing.JButton();
        searchBtncards = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(980, 550));

        parentPanel.setMinimumSize(new java.awt.Dimension(980, 550));
        parentPanel.setPreferredSize(new java.awt.Dimension(980, 550));
        parentPanel.setLayout(new java.awt.CardLayout());

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setName(""); // NOI18N
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Navbar.setBackground(new java.awt.Color(0, 0, 0, 100));
        Navbar.setOpaque(false);

        LogoName.setBackground(new java.awt.Color(255, 255, 255));
        LogoName.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName.setText("<html>Poké<span style=\"color:red;\">Museum</span>\n");
        LogoName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        CardsNav.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CardsNav.setText("Cards");
        CardsNav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardsNavActionPerformed(evt);
            }
        });

        HomeNav.setBackground(new java.awt.Color(102, 255, 255));
        HomeNav.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        HomeNav.setText("Home");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N

        SearchNav.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 12)); // NOI18N
        SearchNav.setForeground(new java.awt.Color(153, 153, 153));
        SearchNav.setText("Search...");

        CollectionNav.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CollectionNav.setText("Collection");
        CollectionNav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CollectionNavActionPerformed(evt);
            }
        });

        RegisterNav.setBackground(new java.awt.Color(102, 102, 255));
        RegisterNav.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        RegisterNav.setForeground(new java.awt.Color(255, 255, 255));
        RegisterNav.setText("Register");
        RegisterNav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterNavActionPerformed(evt);
            }
        });

        LoginNav.setBackground(new java.awt.Color(255, 102, 102));
        LoginNav.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        LoginNav.setForeground(new java.awt.Color(255, 255, 255));
        LoginNav.setText("Login");
        LoginNav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginNavActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NavbarLayout = new javax.swing.GroupLayout(Navbar);
        Navbar.setLayout(NavbarLayout);
        NavbarLayout.setHorizontalGroup(
            NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavbarLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(HomeNav)
                .addGap(18, 18, 18)
                .addComponent(CardsNav)
                .addGap(18, 18, 18)
                .addComponent(CollectionNav)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SearchNav, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(RegisterNav)
                .addGap(18, 18, 18)
                .addComponent(LoginNav)
                .addGap(160, 160, 160))
        );
        NavbarLayout.setVerticalGroup(
            NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavbarLayout.createSequentialGroup()
                .addGroup(NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CardsNav)
                        .addComponent(HomeNav)
                        .addComponent(SearchNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CollectionNav)
                        .addComponent(RegisterNav)
                        .addComponent(LoginNav))
                    .addGroup(NavbarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(Navbar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 60));

        jLabel46.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setText("Featured Cards");
        mainPanel.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 380, -1, -1));

        WelcomePanel.setBackground(new java.awt.Color(255, 255, 255));
        WelcomePanel.setOpaque(false);

        WelcomeText.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        WelcomeText.setText("Welcome To Pokémon Card Museum");

        jLabel1.setText("<html> <p style=\"font-family: Sarabun;\">In this Poké Card Museum, you can find your desired pokémons, <br> view your deck of pokémons, and also filter them with a vast category. <br> <br> Search a pokémon by its <span style =\"color: blue;\">types</span> and <span style=\"color:red;\">name</span></p>");

        javax.swing.GroupLayout WelcomePanelLayout = new javax.swing.GroupLayout(WelcomePanel);
        WelcomePanel.setLayout(WelcomePanelLayout);
        WelcomePanelLayout.setHorizontalGroup(
            WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WelcomePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WelcomePanelLayout.createSequentialGroup()
                        .addComponent(WelcomeText, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, WelcomePanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        WelcomePanelLayout.setVerticalGroup(
            WelcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WelcomePanelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(WelcomeText)
                .addGap(41, 41, 41)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(WelcomePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 420, 160));

        featuredGrid.setLayout(new java.awt.GridLayout(2, 3, 25, 25));
        featuredGrid.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        featuredScroll.setViewportView(featuredGrid);

        mainPanel.add(featuredScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 402, 960, 140));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/allpokemons.png"))); // NOI18N
        jLabel9.setMinimumSize(new java.awt.Dimension(800, 550));
        jLabel9.setPreferredSize(new java.awt.Dimension(700, 550));
        mainPanel.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 990, 550));

        parentPanel.add(mainPanel, "Home");

        loginPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new javax.swing.ImageIcon(getClass().getResource("/utils/stareatwatertypepokemon.jpg")))); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/loginUI.jpg"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 18)); // NOI18N
        jLabel6.setText("Login to your account");

        jLabel7.setText("Username");

        userTF.setToolTipText("");

        jLabel8.setText("Password");

        LoginNav1.setBackground(new java.awt.Color(255, 102, 102));
        LoginNav1.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        LoginNav1.setForeground(new java.awt.Color(255, 255, 255));
        LoginNav1.setText("Login");
        LoginNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginNav1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(56, Short.MAX_VALUE)
                                .addComponent(jLabel6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(passwordField)
                                    .addComponent(userTF, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(45, 45, 45))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(LoginNav1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel6)
                .addGap(29, 29, 29)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(userTF, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(LoginNav1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        loginPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 100, 540, 340));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N
        loginPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, -1, -1));

        LogoName1.setBackground(new java.awt.Color(255, 255, 255));
        LogoName1.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName1.setText("<html><span style=\"color:white;\">Poké</span><span style=\"color:red;\">Museum</span>\n");
        LogoName1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        loginPanel.add(LogoName1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, -1, 62));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png"))); // NOI18N
        loginPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        parentPanel.add(loginPanel, "Login");

        dashHomePanel.setBackground(new java.awt.Color(255, 255, 255));
        dashHomePanel.setName(""); // NOI18N
        dashHomePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        refreshDashboard.setBackground(new java.awt.Color(204, 204, 255));
        refreshDashboard.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 12)); // NOI18N
        refreshDashboard.setText("Refresh Dashboard");
        refreshDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshDashboardActionPerformed(evt);
            }
        });
        dashHomePanel.add(refreshDashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 510, -1, -1));

        jLabel27.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Recently Added Cards");
        dashHomePanel.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 140, -1, -1));

        rareCards.setBorder(javax.swing.BorderFactory.createMatteBorder(4, 4, 4, 4, new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg")))); // NOI18N

        jLabel43.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jLabel43.setText("Most Rare Card in Inventory");

        mostRareCardLabel.setText("Charizard Holo Rare");

        javax.swing.GroupLayout rareCardsLayout = new javax.swing.GroupLayout(rareCards);
        rareCards.setLayout(rareCardsLayout);
        rareCardsLayout.setHorizontalGroup(
            rareCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rareCardsLayout.createSequentialGroup()
                .addGroup(rareCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rareCardsLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel43))
                    .addGroup(rareCardsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mostRareCardLabel)))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        rareCardsLayout.setVerticalGroup(
            rareCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rareCardsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel43)
                .addGap(18, 18, 18)
                .addComponent(mostRareCardLabel)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        dashHomePanel.add(rareCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 410, 240, 90));

        totalCards.setBorder(javax.swing.BorderFactory.createMatteBorder(4, 4, 4, 4, new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg")))); // NOI18N

        jLabel40.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jLabel40.setText("Most Valuable Card in Inventory");

        jLabel41.setFont(new java.awt.Font("Forte", 0, 36)); // NOI18N

        valueCardID.setText("PC0001");

        valueCardName.setText("Charizard Holo Rare");

        valueCardValue.setText("450$");

        javax.swing.GroupLayout totalCardsLayout = new javax.swing.GroupLayout(totalCards);
        totalCards.setLayout(totalCardsLayout);
        totalCardsLayout.setHorizontalGroup(
            totalCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalCardsLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(totalCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(totalCardsLayout.createSequentialGroup()
                        .addComponent(valueCardName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41))
                    .addGroup(totalCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(totalCardsLayout.createSequentialGroup()
                            .addComponent(valueCardID)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valueCardValue))
                        .addComponent(jLabel40)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        totalCardsLayout.setVerticalGroup(
            totalCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalCardsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valueCardID)
                    .addComponent(valueCardValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valueCardName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dashHomePanel.add(totalCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 410, 250, 90));

        totalValue.setBorder(javax.swing.BorderFactory.createMatteBorder(4, 4, 4, 4, new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg")))); // NOI18N

        jLabel42.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jLabel42.setText("Total Cards in Inventory");

        totalCardsLabel.setFont(new java.awt.Font("Forte", 0, 36)); // NOI18N
        totalCardsLabel.setText("5");

        javax.swing.GroupLayout totalValueLayout = new javax.swing.GroupLayout(totalValue);
        totalValue.setLayout(totalValueLayout);
        totalValueLayout.setHorizontalGroup(
            totalValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalValueLayout.createSequentialGroup()
                .addGroup(totalValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(totalValueLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel42))
                    .addGroup(totalValueLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(totalCardsLabel)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        totalValueLayout.setVerticalGroup(
            totalValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalValueLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(totalCardsLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dashHomePanel.add(totalValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 410, 160, 90));

        ValuabelCard.setBorder(javax.swing.BorderFactory.createMatteBorder(4, 4, 4, 4, new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg")))); // NOI18N

        jLabel44.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jLabel44.setText("Total Value of Inventory");

        totalValueLabel.setFont(new java.awt.Font("Forte", 0, 36)); // NOI18N
        totalValueLabel.setText("1100$");

        javax.swing.GroupLayout ValuabelCardLayout = new javax.swing.GroupLayout(ValuabelCard);
        ValuabelCard.setLayout(ValuabelCardLayout);
        ValuabelCardLayout.setHorizontalGroup(
            ValuabelCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ValuabelCardLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(ValuabelCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addGroup(ValuabelCardLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(totalValueLabel)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        ValuabelCardLayout.setVerticalGroup(
            ValuabelCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ValuabelCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalValueLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dashHomePanel.add(ValuabelCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 410, 220, 90));

        Navbar2.setBackground(new java.awt.Color(0, 0, 0, 100));
        Navbar2.setOpaque(false);

        LogoName4.setBackground(new java.awt.Color(255, 255, 255));
        LogoName4.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName4.setText("<html><span style=\"color:white;\">Poké</span><span style=\"color:red;\">Museum</span>\n");
        LogoName4.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        HomeNav2.setBackground(new java.awt.Color(102, 255, 255));
        HomeNav2.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        HomeNav2.setText("Home");
        HomeNav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeNav2ActionPerformed(evt);
            }
        });

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N

        SearchNav2.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 12)); // NOI18N
        SearchNav2.setForeground(new java.awt.Color(153, 153, 153));
        SearchNav2.setText("Search...");

        CollectionNav2.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CollectionNav2.setText("Collection");
        CollectionNav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CollectionNav2ActionPerformed(evt);
            }
        });

        logoutNav.setBackground(new java.awt.Color(255, 102, 102));
        logoutNav.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        logoutNav.setForeground(new java.awt.Color(255, 255, 255));
        logoutNav.setText("Logout");
        logoutNav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutNavActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Navbar2Layout = new javax.swing.GroupLayout(Navbar2);
        Navbar2.setLayout(Navbar2Layout);
        Navbar2Layout.setHorizontalGroup(
            Navbar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoName4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(HomeNav2)
                .addGap(61, 61, 61)
                .addComponent(CollectionNav2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(SearchNav2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99)
                .addComponent(logoutNav)
                .addGap(49, 49, 49))
        );
        Navbar2Layout.setVerticalGroup(
            Navbar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar2Layout.createSequentialGroup()
                .addGroup(Navbar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Navbar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(HomeNav2)
                        .addComponent(SearchNav2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CollectionNav2)
                        .addComponent(logoutNav))
                    .addGroup(Navbar2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel37)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dashHomePanel.add(Navbar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 60));

        recentCards.setBorder(javax.swing.BorderFactory.createMatteBorder(4, 4, 4, 4, new javax.swing.ImageIcon(getClass().getResource("/utils/gengar.png")))); // NOI18N
        recentCards.setLayout(new java.awt.GridLayout(4, 3, 25, 25));
        recentScroll.setViewportView(recentCards);

        dashHomePanel.add(recentScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, 880, 190));

        WelcomePanel1.setBackground(new java.awt.Color(255, 255, 255));
        WelcomePanel1.setOpaque(false);

        WelcomeText1.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        WelcomeText1.setForeground(new java.awt.Color(255, 255, 255));
        WelcomeText1.setText("Welcome To Your Dashboard, Admin!");

        javax.swing.GroupLayout WelcomePanel1Layout = new javax.swing.GroupLayout(WelcomePanel1);
        WelcomePanel1.setLayout(WelcomePanel1Layout);
        WelcomePanel1Layout.setHorizontalGroup(
            WelcomePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WelcomePanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(WelcomeText1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );
        WelcomePanel1Layout.setVerticalGroup(
            WelcomePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WelcomePanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(WelcomeText1)
                .addGap(267, 267, 267))
        );

        dashHomePanel.add(WelcomePanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 420, 40));

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg"))); // NOI18N
        jLabel39.setMinimumSize(new java.awt.Dimension(800, 550));
        jLabel39.setPreferredSize(new java.awt.Dimension(700, 550));
        dashHomePanel.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 990, -1));

        parentPanel.add(dashHomePanel, "Home");

        registerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new javax.swing.ImageIcon(getClass().getResource("/utils/stareatwatertypepokemon.jpg")))); // NOI18N

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/loginUI.jpg"))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 18)); // NOI18N
        jLabel11.setText("Register as a new user");

        jLabel12.setText("Username");

        registerUN.setToolTipText("johndoe@gmail.com");
        registerUN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerUNActionPerformed(evt);
            }
        });

        jLabel13.setText("Password");

        registerPW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerPWActionPerformed(evt);
            }
        });

        LoginNav2.setBackground(new java.awt.Color(0, 51, 204));
        LoginNav2.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        LoginNav2.setForeground(new java.awt.Color(255, 255, 255));
        LoginNav2.setText("Register");
        LoginNav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginNav2ActionPerformed(evt);
            }
        });

        jLabel16.setText("Confirm Password");

        registerCPW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerCPWActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(49, Short.MAX_VALUE)
                                .addComponent(jLabel11))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(registerPW)
                                    .addComponent(registerUN, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel13))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(registerCPW))))
                        .addGap(45, 45, 45))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(LoginNav2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerUN, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerPW, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(registerCPW, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LoginNav2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        registerPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 100, 540, 340));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N
        registerPanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, -1, -1));

        LogoName2.setBackground(new java.awt.Color(255, 255, 255));
        LogoName2.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName2.setText("<html><span style=\"color:white;\">Poké</span><span style=\"color:red;\">Museum</span>\n");
        LogoName2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        registerPanel.add(LogoName2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, -1, 62));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png"))); // NOI18N
        registerPanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        parentPanel.add(registerPanel, "Login");

        collectionPanel.setLayout(new java.awt.CardLayout());

        Navbar1.setBackground(new java.awt.Color(204, 204, 204));

        LogoName3.setBackground(new java.awt.Color(255, 255, 255));
        LogoName3.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName3.setText("<html>Poké<span style=\"color:red;\">Museum</span>\n");
        LogoName3.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        HomeNav1.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        HomeNav1.setText("Home");
        HomeNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeNav1ActionPerformed(evt);
            }
        });

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N

        searchTF.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 12)); // NOI18N
        searchTF.setForeground(new java.awt.Color(153, 153, 153));
        searchTF.setText("Search...");

        CollectionNav1.setBackground(new java.awt.Color(102, 255, 255));
        CollectionNav1.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CollectionNav1.setText("Collection");
        CollectionNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CollectionNav1ActionPerformed(evt);
            }
        });

        logoutNav1.setBackground(new java.awt.Color(255, 102, 102));
        logoutNav1.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        logoutNav1.setForeground(new java.awt.Color(255, 255, 255));
        logoutNav1.setText("Logout");
        logoutNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutNav1ActionPerformed(evt);
            }
        });

        searchBtn.setFont(new java.awt.Font("Gill Sans Ultra Bold", 0, 12)); // NOI18N
        searchBtn.setText("Search");
        searchBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Navbar1Layout = new javax.swing.GroupLayout(Navbar1);
        Navbar1.setLayout(Navbar1Layout);
        Navbar1Layout.setHorizontalGroup(
            Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoName3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(HomeNav1)
                .addGap(65, 65, 65)
                .addComponent(CollectionNav1)
                .addGap(76, 76, 76)
                .addComponent(searchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(logoutNav1)
                .addGap(37, 37, 37))
        );
        Navbar1Layout.setVerticalGroup(
            Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar1Layout.createSequentialGroup()
                .addGroup(Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(HomeNav1)
                        .addComponent(searchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CollectionNav1)
                        .addComponent(logoutNav1)
                        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Navbar1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        collectiongrid.setOpaque(false);

        jLabel18.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 18)); // NOI18N
        jLabel18.setText("Card Collection Inventory");

        addNewCard.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        addNewCard.setText("Add New Card");
        addNewCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewCardActionPerformed(evt);
            }
        });

        editCard.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        editCard.setText("Edit Card");
        editCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCardActionPerformed(evt);
            }
        });

        deleteCard.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        deleteCard.setText("Delete Card");
        deleteCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCardActionPerformed(evt);
            }
        });

        undoDeleteBtn.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        undoDeleteBtn.setText("Undo Delete");
        undoDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoDeleteBtnActionPerformed(evt);
            }
        });

        sortingCB.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        sortingCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort Collection", "By Name", "By Value", "By Rarity" }));
        sortingCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortingCBActionPerformed(evt);
            }
        });

        pokeTable.setBackground(new java.awt.Color(204, 204, 255));
        pokeTable.setFont(new java.awt.Font("Nirmala Text Semilight", 0, 12)); // NOI18N
        pokeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "Pika", null, null, null, null},
                {"2", "Chari", null, null, null, null},
                {"3", "Bulba", null, null, null, null},
                {"4", "Squirtle", null, null, null, null}
            },
            new String [] {
                "PokeID", "Name", "Type", "Rarity", "Condition", "Value"
            }
        ));
        pokeTable.setFocusable(false);
        pokeTable.setRowHeight(30);
        pokeTable.setSelectionBackground(new java.awt.Color(0, 153, 153));
        pokeTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(pokeTable);

        javax.swing.GroupLayout collectiongridLayout = new javax.swing.GroupLayout(collectiongrid);
        collectiongrid.setLayout(collectiongridLayout);
        collectiongridLayout.setHorizontalGroup(
            collectiongridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(collectiongridLayout.createSequentialGroup()
                .addGroup(collectiongridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(collectiongridLayout.createSequentialGroup()
                        .addGroup(collectiongridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(collectiongridLayout.createSequentialGroup()
                                .addGap(362, 362, 362)
                                .addComponent(jLabel18))
                            .addGroup(collectiongridLayout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(addNewCard)
                                .addGap(43, 43, 43)
                                .addComponent(editCard)
                                .addGap(67, 67, 67)
                                .addComponent(deleteCard)
                                .addGap(52, 52, 52)
                                .addComponent(undoDeleteBtn)
                                .addGap(53, 53, 53)
                                .addComponent(sortingCB, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 144, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, collectiongridLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 964, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        collectiongridLayout.setVerticalGroup(
            collectiongridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(collectiongridLayout.createSequentialGroup()
                .addComponent(jLabel18)
                .addGap(14, 14, 14)
                .addGroup(collectiongridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortingCB, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteCard, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editCard, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addNewCard, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(undoDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout cgPanelLayout = new javax.swing.GroupLayout(cgPanel);
        cgPanel.setLayout(cgPanelLayout);
        cgPanelLayout.setHorizontalGroup(
            cgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(collectiongrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(Navbar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        cgPanelLayout.setVerticalGroup(
            cgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cgPanelLayout.createSequentialGroup()
                .addComponent(Navbar1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(collectiongrid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        collectionPanel.add(cgPanel, "card7");

        addCardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png")))); // NOI18N

        jLabel20.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 18)); // NOI18N
        jLabel20.setText("Add a New Pokemon Card");

        jLabel21.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel21.setText("PokeID:");

        jLabel22.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel22.setText("Card Name:");

        jLabel23.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel23.setText("Pokemon type:");

        jLabel24.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel24.setText("Rarity:");

        jLabel25.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel25.setText("Condition");

        jLabel26.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel26.setText("Value:");

        idTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idTFActionPerformed(evt);
            }
        });

        typeCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy" }));

        rarityCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Common", "Uncommon", "Rare", "Epic", "Legendary", "SSS", "SSS+" }));

        conditionCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Near Mint", "Lightly Played", "Moderately Played", "Heavily Played", "Damaged" }));

        cancelAddBtn.setFont(new java.awt.Font("Bauhaus 93", 0, 14)); // NOI18N
        cancelAddBtn.setText("Cancel");
        cancelAddBtn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png")))); // NOI18N
        cancelAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAddBtnActionPerformed(evt);
            }
        });

        saveCardBtn.setBackground(new java.awt.Color(51, 51, 51));
        saveCardBtn.setFont(new java.awt.Font("Bauhaus 93", 0, 14)); // NOI18N
        saveCardBtn.setForeground(new java.awt.Color(255, 255, 255));
        saveCardBtn.setText("Save Card");
        saveCardBtn.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png")))); // NOI18N
        saveCardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCardBtnActionPerformed(evt);
            }
        });

        btnChooseImage.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnChooseImage.setText("Choose an Image");
        btnChooseImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });

        jLabel54.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel54.setText(" Pokemon Card:");

        lblImagePreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/pokecard.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel25)
                                    .addComponent(jLabel26))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(typeCB, 0, 132, Short.MAX_VALUE)
                                        .addComponent(valueTF))
                                    .addComponent(nameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rarityCB, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(idTF, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(conditionCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnChooseImage, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(lblImagePreview)
                        .addGap(0, 22, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(cancelAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(saveCardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addComponent(jLabel20)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel20)
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImagePreview)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(idTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(nameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(typeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(rarityCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(conditionCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(valueTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnChooseImage, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelAddBtn)
                    .addComponent(saveCardBtn))
                .addGap(26, 26, 26))
        );

        addCardPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 80, 510, 400));

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/stareatwatertypepokemon.jpg"))); // NOI18N
        addCardPanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        collectionPanel.add(addCardPanel, "card4");

        editCardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png")))); // NOI18N

        jLabel28.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 18)); // NOI18N
        jLabel28.setText("Edit this Pokemon Card");

        jLabel29.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel29.setText("PokeID:");

        jLabel30.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel30.setText("Card Name:");

        jLabel31.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel31.setText("Pokemon type:");

        jLabel32.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel32.setText("Rarity:");

        jLabel33.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel33.setText("Condition");

        jLabel34.setFont(new java.awt.Font("Gill Sans MT", 0, 14)); // NOI18N
        jLabel34.setText("Value:");

        idTF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idTF1ActionPerformed(evt);
            }
        });

        typeCB1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy" }));

        rarityCB1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Common", "Uncommon", "Rare", "Epic", "Legendary", "SSS", "SSS+" }));

        conditionCB1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Near Mint", "Lightly Played", "Moderately Played", "Heavily Played", "Damaged" }));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/pokecard.jpg"))); // NOI18N

        cancelAddBtn1.setFont(new java.awt.Font("Bauhaus 93", 0, 14)); // NOI18N
        cancelAddBtn1.setText("Cancel");
        cancelAddBtn1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png")))); // NOI18N
        cancelAddBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAddBtn1ActionPerformed(evt);
            }
        });

        saveCardBtn1.setBackground(new java.awt.Color(51, 51, 51));
        saveCardBtn1.setFont(new java.awt.Font("Bauhaus 93", 0, 14)); // NOI18N
        saveCardBtn1.setForeground(new java.awt.Color(255, 255, 255));
        saveCardBtn1.setText("Update Card");
        saveCardBtn1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new javax.swing.ImageIcon(getClass().getResource("/utils/coolblue.png")))); // NOI18N
        saveCardBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCardBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31)
                            .addComponent(jLabel30)
                            .addComponent(jLabel29)
                            .addComponent(jLabel32)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(idTF1)
                            .addComponent(nameTF1)
                            .addComponent(typeCB1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rarityCB1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(conditionCB1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valueTF1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jLabel35)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(cancelAddBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(saveCardBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(idTF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(nameTF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(typeCB1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32)
                            .addComponent(rarityCB1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(conditionCB1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(valueTF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelAddBtn1)
                    .addComponent(saveCardBtn1))
                .addGap(21, 21, 21))
        );

        editCardPanel.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 80, 510, 390));

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/stareatwatertypepokemon.jpg"))); // NOI18N
        editCardPanel.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        collectionPanel.add(editCardPanel, "card4");

        parentPanel.add(collectionPanel, "card6");

        userPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        WelcomeText2.setFont(new java.awt.Font("Gill Sans MT", 1, 18)); // NOI18N
        WelcomeText2.setForeground(new java.awt.Color(255, 255, 255));
        WelcomeText2.setText("Our Top Cards");
        userPanel.add(WelcomeText2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 190, -1, -1));

        WelcomeText3.setFont(new java.awt.Font("Gill Sans MT", 1, 24)); // NOI18N
        WelcomeText3.setForeground(new java.awt.Color(255, 255, 255));
        WelcomeText3.setText("Welcome To Pokémon Card Museum");
        userPanel.add(WelcomeText3, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, -1, -1));

        Navbar3.setBackground(new java.awt.Color(0, 0, 0, 100));
        Navbar3.setOpaque(false);

        LogoName5.setBackground(new java.awt.Color(255, 255, 255));
        LogoName5.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName5.setText("<html><span style=\"color:white;\">Poké</span><span style=\"color:red;\">Museum</span>\n");
        LogoName5.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        CardsNav3.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CardsNav3.setText("Cards");
        CardsNav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardsNav3ActionPerformed(evt);
            }
        });

        HomeNav3.setBackground(new java.awt.Color(102, 255, 255));
        HomeNav3.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        HomeNav3.setText("Home");

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N

        SearchNav1.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 12)); // NOI18N
        SearchNav1.setForeground(new java.awt.Color(153, 153, 153));
        SearchNav1.setText("Search...");

        CollectionNav3.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CollectionNav3.setText("Collection");
        CollectionNav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CollectionNav3ActionPerformed(evt);
            }
        });

        LoginNav3.setBackground(new java.awt.Color(255, 102, 102));
        LoginNav3.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        LoginNav3.setForeground(new java.awt.Color(255, 255, 255));
        LoginNav3.setText("Logout");
        LoginNav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginNav3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Navbar3Layout = new javax.swing.GroupLayout(Navbar3);
        Navbar3.setLayout(Navbar3Layout);
        Navbar3Layout.setHorizontalGroup(
            Navbar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoName5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(HomeNav3)
                .addGap(18, 18, 18)
                .addComponent(CardsNav3)
                .addGap(18, 18, 18)
                .addComponent(CollectionNav3)
                .addGap(34, 34, 34)
                .addComponent(SearchNav1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addComponent(LoginNav3)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        Navbar3Layout.setVerticalGroup(
            Navbar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar3Layout.createSequentialGroup()
                .addGroup(Navbar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Navbar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CardsNav3)
                        .addComponent(HomeNav3)
                        .addComponent(SearchNav1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CollectionNav3)
                        .addComponent(LoginNav3))
                    .addGroup(Navbar3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel45)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        userPanel.add(Navbar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 60));

        seeAllBtn.setBackground(new java.awt.Color(0, 153, 153));
        seeAllBtn.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        seeAllBtn.setForeground(new java.awt.Color(255, 255, 255));
        seeAllBtn.setText("See All");
        seeAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeAllBtnActionPerformed(evt);
            }
        });
        userPanel.add(seeAllBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 220, -1, -1));

        topScroll.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new javax.swing.ImageIcon(getClass().getResource("/utils/blastoise.png")))); // NOI18N

        topCardGrid.setLayout(new java.awt.GridLayout(2, 3, 25, 25));
        topCardGrid.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        topScroll.setViewportView(topCardGrid);

        userPanel.add(topScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 252, 960, 290));

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/watah.jpg"))); // NOI18N
        jLabel38.setPreferredSize(new java.awt.Dimension(980, 550));
        userPanel.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, -1));

        parentPanel.add(userPanel, "card7");

        userCards.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cardsGrid.setLayout(new java.awt.GridLayout(6, 3, 20, 20));
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        cardsScroll.setViewportView(cardsGrid);

        addInv.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 12)); // NOI18N
        addInv.setText("Add Cards to Inventory");
        addInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addInvActionPerformed(evt);
            }
        });

        sortCardsCB.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        sortCardsCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort Collection", "By Name", "By Value", "By Rarity" }));
        sortCardsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortCardsCBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cardsShowPanelLayout = new javax.swing.GroupLayout(cardsShowPanel);
        cardsShowPanel.setLayout(cardsShowPanelLayout);
        cardsShowPanelLayout.setHorizontalGroup(
            cardsShowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardsShowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cardsScroll)
                .addContainerGap())
            .addGroup(cardsShowPanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(addInv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 497, Short.MAX_VALUE)
                .addComponent(sortCardsCB, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122))
        );
        cardsShowPanelLayout.setVerticalGroup(
            cardsShowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardsShowPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(cardsShowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addInv)
                    .addComponent(sortCardsCB, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cardsScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );

        userCards.add(cardsShowPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 960, 440));

        Navbar4.setBackground(new java.awt.Color(0, 0, 0, 100));
        Navbar4.setOpaque(false);

        LogoName6.setBackground(new java.awt.Color(255, 255, 255));
        LogoName6.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName6.setText("<html><span style=\"color:white;\">Poké</span><span style=\"color:red;\">Museum</span>\n");
        LogoName6.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        CardsNav4.setBackground(new java.awt.Color(102, 255, 255));
        CardsNav4.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CardsNav4.setText("Cards");
        CardsNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardsNav4ActionPerformed(evt);
            }
        });

        HomeNav4.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        HomeNav4.setText("Home");
        HomeNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeNav4ActionPerformed(evt);
            }
        });

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N

        searchTFcards.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 12)); // NOI18N
        searchTFcards.setForeground(new java.awt.Color(153, 153, 153));
        searchTFcards.setText("Search...");

        CollectionNav4.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CollectionNav4.setText("Collection");
        CollectionNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CollectionNav4ActionPerformed(evt);
            }
        });

        LoginNav4.setBackground(new java.awt.Color(255, 102, 102));
        LoginNav4.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        LoginNav4.setForeground(new java.awt.Color(255, 255, 255));
        LoginNav4.setText("Logout");
        LoginNav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginNav4ActionPerformed(evt);
            }
        });

        searchBtncards2.setFont(new java.awt.Font("Gill Sans Ultra Bold", 0, 12)); // NOI18N
        searchBtncards2.setText("Search");
        searchBtncards2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchBtncards2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtncards2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Navbar4Layout = new javax.swing.GroupLayout(Navbar4);
        Navbar4.setLayout(Navbar4Layout);
        Navbar4Layout.setHorizontalGroup(
            Navbar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoName6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(HomeNav4)
                .addGap(18, 18, 18)
                .addComponent(CardsNav4)
                .addGap(18, 18, 18)
                .addComponent(CollectionNav4)
                .addGap(22, 22, 22)
                .addComponent(searchTFcards, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(searchBtncards2)
                .addGap(18, 18, 18)
                .addComponent(LoginNav4)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        Navbar4Layout.setVerticalGroup(
            Navbar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar4Layout.createSequentialGroup()
                .addGroup(Navbar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Navbar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CardsNav4)
                        .addComponent(HomeNav4)
                        .addComponent(searchTFcards, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CollectionNav4)
                        .addComponent(LoginNav4)
                        .addComponent(searchBtncards2))
                    .addGroup(Navbar4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel49)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        userCards.add(Navbar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 60));

        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg"))); // NOI18N
        userCards.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        parentPanel.add(userCards, "card8");

        userCollection.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Navbar5.setBackground(new java.awt.Color(0, 0, 0, 100));
        Navbar5.setOpaque(false);

        LogoName7.setBackground(new java.awt.Color(255, 255, 255));
        LogoName7.setFont(new java.awt.Font("Eras Light ITC", 1, 24)); // NOI18N
        LogoName7.setText("<html><span style=\"color:white;\">Poké</span><span style=\"color:red;\">Museum</span>\n");
        LogoName7.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        CardsNav5.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CardsNav5.setText("Cards");
        CardsNav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardsNav5ActionPerformed(evt);
            }
        });

        HomeNav5.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        HomeNav5.setText("Home");
        HomeNav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeNav5ActionPerformed(evt);
            }
        });

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/PokeLogo.png"))); // NOI18N

        invSearchTF.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 12)); // NOI18N
        invSearchTF.setForeground(new java.awt.Color(153, 153, 153));
        invSearchTF.setText("Search...");

        CollectionNav5.setBackground(new java.awt.Color(102, 255, 255));
        CollectionNav5.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CollectionNav5.setText("Collection");
        CollectionNav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CollectionNav5ActionPerformed(evt);
            }
        });

        LoginNav5.setBackground(new java.awt.Color(255, 102, 102));
        LoginNav5.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        LoginNav5.setForeground(new java.awt.Color(255, 255, 255));
        LoginNav5.setText("Logout");
        LoginNav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginNav5ActionPerformed(evt);
            }
        });

        searchBtncards1.setFont(new java.awt.Font("Gill Sans Ultra Bold", 0, 12)); // NOI18N
        searchBtncards1.setText("Search");
        searchBtncards1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchBtncards1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtncards1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Navbar5Layout = new javax.swing.GroupLayout(Navbar5);
        Navbar5.setLayout(Navbar5Layout);
        Navbar5Layout.setHorizontalGroup(
            Navbar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar5Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoName7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(HomeNav5)
                .addGap(18, 18, 18)
                .addComponent(CardsNav5)
                .addGap(18, 18, 18)
                .addComponent(CollectionNav5)
                .addGap(22, 22, 22)
                .addComponent(invSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(searchBtncards1)
                .addGap(18, 18, 18)
                .addComponent(LoginNav5)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        Navbar5Layout.setVerticalGroup(
            Navbar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar5Layout.createSequentialGroup()
                .addGroup(Navbar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Navbar5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName7, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CardsNav5)
                        .addComponent(HomeNav5)
                        .addComponent(invSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CollectionNav5)
                        .addComponent(LoginNav5)
                        .addComponent(searchBtncards1))
                    .addGroup(Navbar5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel50)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        userCollection.add(Navbar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 60));

        inventoryGrid.setLayout(new java.awt.GridLayout(6, 3, 20, 20));
        inventoryGrid.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        inventoryScroll.setViewportView(inventoryGrid);

        removeSelectedBtn1.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 12)); // NOI18N
        removeSelectedBtn1.setText("Remove Cards From Collection");
        removeSelectedBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSelectedBtn1ActionPerformed(evt);
            }
        });

        sortCards1.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        sortCards1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort Collection", "By Name", "By Value", "By Rarity" }));
        sortCards1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortCards1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout inventoryShowLayout = new javax.swing.GroupLayout(inventoryShow);
        inventoryShow.setLayout(inventoryShowLayout);
        inventoryShowLayout.setHorizontalGroup(
            inventoryShowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryShowLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inventoryScroll)
                .addContainerGap())
            .addGroup(inventoryShowLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(removeSelectedBtn1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 496, Short.MAX_VALUE)
                .addComponent(sortCards1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
        );
        inventoryShowLayout.setVerticalGroup(
            inventoryShowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryShowLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(inventoryShowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeSelectedBtn1)
                    .addComponent(sortCards1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inventoryScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );

        userCollection.add(inventoryShow, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 960, 440));

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/collectionsbg.jpeg"))); // NOI18N
        userCollection.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        removeSelectedBtn.setText("jButton1");
        userCollection.add(removeSelectedBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, -1, -1));

        searchBtncards.setText("jButton1");
        userCollection.add(searchBtncards, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 70, -1, -1));

        parentPanel.add(userCollection, "card9");

        getContentPane().add(parentPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LoginNav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginNav1ActionPerformed
        String username = userTF.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter username and password!", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate login using your User model
        if (user.validateLogin(username, password)) {
            // Get the users role
            String role = user.getUserRole(username);

            if (role == null) {
                JOptionPane.showMessageDialog(this, "Role not found for user!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Store current user info
            currentUsername = username;
            currentUserRole = role;
            currentUser = user.getUserByUsername(username);

            passwordField.setText("");

            // Redirect based on role
            parentPanel.removeAll();

            if ("admin".equalsIgnoreCase(role)) {
                // Admin -> Dashboard
                parentPanel.add(dashHomePanel);
                refreshDashboard();  // update stats, recent cards, etc.
                JOptionPane.showMessageDialog(this, "Welcome Admin: " + username, "Login Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Regular user -> User panel (inventory/browse)
                parentPanel.add(userPanel);
                JOptionPane.showMessageDialog(this, "Welcome " + username + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
            }

            parentPanel.revalidate();
            parentPanel.repaint();
            updateUndoButtonState();
            updateRemoveButtonState();

            // Optional: Hide login/register buttons in navbar, show logout
            LoginNav.setVisible(false);
            RegisterNav.setVisible(false);
            logoutNav.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password!", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_LoginNav1ActionPerformed

    private void CardsNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardsNavActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        JOptionPane.showMessageDialog(this, 
            "Please login first to access the collection!", 
            "Access Denied", 
            JOptionPane.WARNING_MESSAGE,
            pokeIcon);
    }//GEN-LAST:event_CardsNavActionPerformed

    private void CollectionNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNavActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        JOptionPane.showMessageDialog(this, 
            "Please login first to access the collection!", 
            "Access Denied", 
            JOptionPane.WARNING_MESSAGE,
            pokeIcon);
    }//GEN-LAST:event_CollectionNavActionPerformed

    private void LoginNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginNavActionPerformed
        parentPanel.removeAll();
        parentPanel.add(loginPanel);
        parentPanel.revalidate();
        parentPanel.repaint();
        
        userTF.setText("");
        passwordField.setText("");
    }//GEN-LAST:event_LoginNavActionPerformed

    private void LoginNav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginNav2ActionPerformed
        String username = registerUN.getText().trim();
        String password = new String(registerPW.getPassword()).trim();
        String confirmPassword = new String(registerCPW.getPassword()).trim();

        // Basic validation
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username cannot be empty!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Password cannot be empty!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try to add the new user
        boolean success = user.addUser(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful!\nYou can now login.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

            // Clear fields
            registerUN.setText("");
            registerPW.setText("");
            registerCPW.setText("");

            parentPanel.removeAll();
            parentPanel.add(loginPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, 
            "Registration failed!\nUsername already exists.", 
            "Registration Error", 
            JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_LoginNav2ActionPerformed

    private void registerUNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerUNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registerUNActionPerformed

    private void registerPWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerPWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registerPWActionPerformed

    private void registerCPWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerCPWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registerCPWActionPerformed

    private void CollectionNav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CollectionNav1ActionPerformed

    private void logoutNav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutNav1ActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            pokeIcon);

        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentUserRole = "";
            currentUser = null;
            currentUsername = "";
            //user = yes = logout
        
            //switch back to Home
            parentPanel.removeAll();
            parentPanel.add(mainPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
            
            LoginNav.setVisible(true);
            RegisterNav.setVisible(true);
            logoutNav.setVisible(false);
        
            JOptionPane.showMessageDialog(this, "You have been logged out successfully!", 
                "Logged Out", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_logoutNav1ActionPerformed

    private void addNewCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewCardActionPerformed
        collectionPanel.removeAll();
        collectionPanel.add(addCardPanel);
        collectionPanel.repaint();
        collectionPanel.revalidate();
        
        //Clear form fields for new entry
        idTF.setText("");
        nameTF.setText("");
        typeCB.setSelectedIndex(0);
        rarityCB.setSelectedIndex(0);
        conditionCB.setSelectedIndex(0);
        valueTF.setText("");
    }//GEN-LAST:event_addNewCardActionPerformed

    private void deleteCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCardActionPerformed
        int row = pokeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a card to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) pokeTable.getModel();
        String id = (String) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete card " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteCard(id);
                refreshCardTable(controller.readAllCards());  
                refreshDashboard();
                updateUndoButtonState();
                System.out.println("DEBUG: After delete - undo button enabled? " + undoDeleteBtn.isEnabled());
                JOptionPane.showMessageDialog(this, "Card deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteCardActionPerformed

    private void HomeNav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeNav1ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(dashHomePanel);
        parentPanel.repaint();
        parentPanel.revalidate();
        refreshDashboard();
    }//GEN-LAST:event_HomeNav1ActionPerformed

    private void idTF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idTF1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idTF1ActionPerformed

    private void cancelAddBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAddBtn1ActionPerformed
        collectionPanel.removeAll();
        collectionPanel.add(cgPanel);
        collectionPanel.repaint();
        collectionPanel.revalidate(); 
    }//GEN-LAST:event_cancelAddBtn1ActionPerformed

    private void saveCardBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCardBtn1ActionPerformed
        try {
            String id = idTF1.getText().trim();
            String name = nameTF1.getText().trim();
            String type = (String) typeCB1.getSelectedItem();
            String rarity = (String) rarityCB1.getSelectedItem();
            String condition = (String) conditionCB1.getSelectedItem();
            double value = Double.parseDouble(valueTF1.getText().trim());

            controller.updateCard(id, name, type, rarity, condition, value);

            JOptionPane.showMessageDialog(this, "Card updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Back to grid
            collectionPanel.removeAll();
            collectionPanel.add(cgPanel);
            collectionPanel.revalidate();
            collectionPanel.repaint();
            refreshCardTable(controller.readAllCards());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_saveCardBtn1ActionPerformed

    private void editCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCardActionPerformed
        int row = pokeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a card to edit!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return; // Stop if nothing selected
        }
        DefaultTableModel model = (DefaultTableModel) pokeTable.getModel();
        // Fill the edit form with selected card data
        idTF1.setText((String) model.getValueAt(row, 0));
        nameTF1.setText((String) model.getValueAt(row, 1));
        typeCB1.setSelectedItem(model.getValueAt(row, 2));
        rarityCB1.setSelectedItem(model.getValueAt(row, 3));
        conditionCB1.setSelectedItem(model.getValueAt(row, 4));
        valueTF1.setText(model.getValueAt(row, 5).toString());
        
        // Switch to Edit panel
        collectionPanel.removeAll();
        collectionPanel.add(editCardPanel);
        collectionPanel.revalidate();
        collectionPanel.repaint();
    }//GEN-LAST:event_editCardActionPerformed

    private void CollectionNav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav2ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(collectionPanel);
        parentPanel.repaint();
        parentPanel.revalidate();
        
        refreshCardTable(controller.readAllCards());
        updateUndoButtonState();
    }//GEN-LAST:event_CollectionNav2ActionPerformed

    private void logoutNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutNavActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            pokeIcon);

        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentUserRole = "";
            currentUser = null;
            currentUsername = "";
            //user = yes = logout
        
            //switch back to Home
            parentPanel.removeAll();
            parentPanel.add(mainPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
            
            LoginNav.setVisible(true);
            RegisterNav.setVisible(true);
            logoutNav.setVisible(false);
        
            JOptionPane.showMessageDialog(this, "You have been logged out successfully!", 
                "Logged Out", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_logoutNavActionPerformed

    private void btnChooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseImageActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Pokémon Card Image");
    
        //filter only images
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
    
        int result = fileChooser.showOpenDialog(MainFrame.this);
    
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            // Show preview
            try {
                ImageIcon icon = new ImageIcon(selectedImagePath);
                Image scaled = icon.getImage().getScaledInstance(
                    lblImagePreview.getWidth(), 
                    lblImagePreview.getHeight(), 
                    Image.SCALE_SMOOTH
                );
                lblImagePreview.setIcon(new ImageIcon(scaled));
                lblImagePreview.setText(""); // remove "No image" text
            } catch (Exception ex) {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Cannot load image");
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Failed to load image preview", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnChooseImageActionPerformed

    private void saveCardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCardBtnActionPerformed
        try {
            String id = idTF.getText().trim();
            String name = nameTF.getText().trim();
            String type = (String) typeCB.getSelectedItem();
            String rarity = (String) rarityCB.getSelectedItem();
            String condition = (String) conditionCB.getSelectedItem();
            double value = Double.parseDouble(valueTF.getText().trim());
            String imagePath = selectedImagePath;
            
            if (imagePath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an image!", 
                                         "Missing Image", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.createCard(id, name, type, rarity, condition, value, imagePath);

            JOptionPane.showMessageDialog(this, "Card added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Back to grid and refresh
            collectionPanel.removeAll();
            collectionPanel.add(cgPanel); // or your grid panel
            collectionPanel.revalidate();
            collectionPanel.repaint();
            refreshCardTable(controller.readAllCards());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Value must be a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_saveCardBtnActionPerformed

    private void cancelAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAddBtnActionPerformed
        collectionPanel.removeAll();
        collectionPanel.add(cgPanel);
        collectionPanel.repaint();
        collectionPanel.revalidate();
    }//GEN-LAST:event_cancelAddBtnActionPerformed

    private void idTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idTFActionPerformed

    private void undoDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoDeleteBtnActionPerformed
        updateUndoButtonState();
        performUndoDelete();
    }//GEN-LAST:event_undoDeleteBtnActionPerformed

    private void RegisterNavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterNavActionPerformed
        parentPanel.removeAll();
        parentPanel.add(registerPanel);
        parentPanel.revalidate();
        parentPanel.repaint();
        
        userTF.setText("");
        passwordField.setText("");
    }//GEN-LAST:event_RegisterNavActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        String query = searchTF.getText().trim();
    
        if (query.isEmpty()) {
            refreshCardTable(controller.readAllCards());  // show all
            return;
        }

        List<PokeCard> results = new ArrayList<>();

        //For id search, when user inputs id (needs to be exact)
        PokeCard byId = controller.searchByIdHash(query);
        if (byId != null) {
            results.add(byId);
        } 
        //If no ID match -> do name search (partial)
        else {
            results = controller.searchByNameLinear(query);
        }

        //Finally try value if itts a number and no results yet
        if (results.isEmpty()) {
            try {
                double val = Double.parseDouble(query);
                PokeCard byValue = controller.searchByValueBinary(val);
                if (byValue != null) {
                    results.add(byValue);
                }
            } catch (NumberFormatException ignored) {
            // not a number = ignore
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No cards found for: " + query, 
                "Search Result", 
                JOptionPane.INFORMATION_MESSAGE);
        }

        refreshCardTable(results);
    }//GEN-LAST:event_searchBtnActionPerformed

    private void refreshDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshDashboardActionPerformed
        refreshDashboard();
    }//GEN-LAST:event_refreshDashboardActionPerformed

    private void HomeNav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeNav2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HomeNav2ActionPerformed

    private void sortingCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortingCBActionPerformed
        String selectedSort = (String) sortingCB.getSelectedItem();
        List<PokeCard> sortedCards;

        switch (selectedSort) {
            case "By Value":
                sortedCards = controller.getCardsSortedByValue();
                break;

            case "By Name":
                sortedCards = controller.getCardsSortedByName();
                break;

            case "By Rarity":
                sortedCards = controller.getCardsSortedByRarity();
                break;

            default: // Sorting/None - Original Order
                sortedCards = controller.readAllCards();
                break;
        }

        //Refresh table with sorted list
        refreshCardTable(sortedCards);

        JOptionPane.showMessageDialog(this, 
        "Table sorted by: " + selectedSort, 
        "Sorting Applied", 
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_sortingCBActionPerformed

    private void CardsNav3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardsNav3ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userCards);
        parentPanel.repaint();
        parentPanel.revalidate();
        
        refreshCollectionView(controller.readAllCards());
    }//GEN-LAST:event_CardsNav3ActionPerformed

    private void CollectionNav3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav3ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userCollection);
        parentPanel.repaint();
        parentPanel.revalidate();
        
    }//GEN-LAST:event_CollectionNav3ActionPerformed

    private void LoginNav3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginNav3ActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            pokeIcon);

        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentUserRole = "";
            currentUser = null;
            currentUsername = "";
            //user = yes = logout
        
            //switch back to Home
            parentPanel.removeAll();
            parentPanel.add(mainPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
            
            LoginNav.setVisible(true);
            RegisterNav.setVisible(true);
            logoutNav.setVisible(false);
        
            JOptionPane.showMessageDialog(this, "You have been logged out successfully!", 
                "Logged Out", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_LoginNav3ActionPerformed

    private void CardsNav4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardsNav4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CardsNav4ActionPerformed

    private void CollectionNav4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav4ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userCollection);
        parentPanel.repaint();
        parentPanel.revalidate();
        
        refreshUserInventory();
        updateRemoveButtonState();
    }//GEN-LAST:event_CollectionNav4ActionPerformed

    private void LoginNav4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginNav4ActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            pokeIcon);

        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentUserRole = "";
            currentUser = null;
            currentUsername = "";
            //user = yes = logout
        
            //switch back to Home
            parentPanel.removeAll();
            parentPanel.add(mainPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
            
            LoginNav.setVisible(true);
            RegisterNav.setVisible(true);
            logoutNav.setVisible(false);
        
            JOptionPane.showMessageDialog(this, "You have been logged out successfully!", 
                "Logged Out", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_LoginNav4ActionPerformed

    private void searchBtncards2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtncards2ActionPerformed
        String query = searchTFcards.getText().trim();
    
        if (query.isEmpty()) {
            refreshCollectionView(controller.readAllCards());  // show all
            return;
        }

        List<PokeCard> results = new ArrayList<>();

        //For id search, when user inputs id (needs to be exact)
        PokeCard byId = controller.searchByIdHash(query);
        if (byId != null) {
            results.add(byId);
        } 
        //If no ID match -> do name search (partial)
        else {
            results = controller.searchByNameLinear(query);
        }

        //Finally try value if itts a number and no results yet
        if (results.isEmpty()) {
            try {
                double val = Double.parseDouble(query);
                PokeCard byValue = controller.searchByValueBinary(val);
                if (byValue != null) {
                    results.add(byValue);
                }
            } catch (NumberFormatException ignored) {
            // not a number = ignore
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No cards found for: " + query, 
                "Search Result", 
                JOptionPane.INFORMATION_MESSAGE);
        }

        refreshCollectionView(results);
    }//GEN-LAST:event_searchBtncards2ActionPerformed
    // TODO add your handling code here:


    private void sortCardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortingCB1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sortingCB1ActionPerformed

    private void sortCardsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortCardsCBActionPerformed
        
        String choice = (String) sortCardsCB.getSelectedItem();
        List<PokeCard> sortt;

        switch (choice) {
            case "By Value":
                sortt = controller.getCardsSortedByValue();
                break;

            case "By Name":
                sortt = controller.getCardsSortedByName();
                break;

            case "By Rarity":
                sortt = controller.getCardsSortedByRarity();
                break;

            default: // Sorting/None - Original Order
                sortt = controller.readAllCards();
                break;
        }
        refreshCollectionView(sortt);
    }//GEN-LAST:event_sortCardsCBActionPerformed

    private void addInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addInvActionPerformed
        // Safety check
        if (currentUser == null || !"user".equalsIgnoreCase(currentUserRole)) {
            JOptionPane.showMessageDialog(this,
                "You must be logged in as a regular user to add cards to inventory!",
                "Access Denied",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCards.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No cards selected to add!",
                "Nothing Selected",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int addedCount = 0;
        int alreadyExistsCount = 0;

        // Make a copy to avoid concurrent modification during iteration
        Set<PokeCard> toAdd = new HashSet<>(selectedCards);

        for (PokeCard card : toAdd) {
            if (currentUser.addToInventory(card)) {
                addedCount++;
            } else {
                alreadyExistsCount++;
            }
        }

        String message;
        if (addedCount > 0) {
            message = addedCount + " card(s) added successfully to your inventory!";
            if (alreadyExistsCount > 0) {
                message += "\n" + alreadyExistsCount + " were already in your inventory.";
            }
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "All selected cards were already in your inventory!",
                "No New Additions",
                JOptionPane.INFORMATION_MESSAGE);
        }

        // Clear selection
        selectedCards.clear();
        updateAddButtonState();// disable button again

        // Refresh the browse view (uncheck all checkboxes)
        refreshCollectionView(controller.readAllCards());
        refreshUserInventory();
    }//GEN-LAST:event_addInvActionPerformed

    private void seeAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeAllBtnActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userCards);
        parentPanel.repaint();
        parentPanel.revalidate();
        
        refreshCollectionView(controller.readAllCards());
    }//GEN-LAST:event_seeAllBtnActionPerformed

    private void HomeNav4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeNav4ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userPanel);
        parentPanel.repaint();
        parentPanel.revalidate();
    }//GEN-LAST:event_HomeNav4ActionPerformed

    private void CardsNav5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardsNav5ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userCards);
        parentPanel.repaint();
        parentPanel.revalidate();
        
        refreshCollectionView(controller.readAllCards());
    }//GEN-LAST:event_CardsNav5ActionPerformed

    private void HomeNav5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeNav5ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(userPanel);
        parentPanel.repaint();
        parentPanel.revalidate();
    }//GEN-LAST:event_HomeNav5ActionPerformed

    private void CollectionNav5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CollectionNav5ActionPerformed

    private void LoginNav5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginNav5ActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE,
            pokeIcon);

        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentUserRole = "";
            currentUser = null;
            currentUsername = "";
            //user = yes = logout
        
            //switch back to Home
            parentPanel.removeAll();
            parentPanel.add(mainPanel);
            parentPanel.revalidate();
            parentPanel.repaint();
            
            LoginNav.setVisible(true);
            RegisterNav.setVisible(true);
            logoutNav.setVisible(false);
        
            JOptionPane.showMessageDialog(this, "You have been logged out successfully!", 
                "Logged Out", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_LoginNav5ActionPerformed

    private void searchBtncards1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtncards1ActionPerformed
        String query = invSearchTF.getText().trim();
    
        if (query.isEmpty()) {
            refreshUserInventory();
            return;
        }

        List<PokeCard> filtered = new ArrayList<>();

        //For id search, when user inputs id (needs to be exact)
        PokeCard byId = controller.searchByIdHash(query);
        if (byId != null) {
            filtered.add(byId);
        } 
        //If no ID match -> do name search (partial)
        else {
            filtered = controller.searchByNameLinear(query);
        }

        //Finally try value if itts a number and no results yet
        if (filtered.isEmpty()) {
            try {
                double val = Double.parseDouble(query);
                PokeCard byValue = controller.searchByValueBinary(val);
                if (byValue != null) {
                    filtered.add(byValue);
                }
            } catch (NumberFormatException ignored) {
            // not a number = ignore
            }
        }

        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No cards found for: " + query, 
                "Search Result", 
                JOptionPane.INFORMATION_MESSAGE);
        }

        refreshUserInventory(filtered);
    }//GEN-LAST:event_searchBtncards1ActionPerformed

    private void sortCards1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortCards1ActionPerformed
        sortInventory();
    }//GEN-LAST:event_sortCards1ActionPerformed

    private void removeSelectedBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSelectedBtn1ActionPerformed
        removeSelectedFromInventory();
        updateRemoveButtonState();
    }//GEN-LAST:event_removeSelectedBtn1ActionPerformed

    private void searchBtncardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchBtn1ActionPerformed

    private void removeSelectedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addInv1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addInv1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CardsNav;
    private javax.swing.JButton CardsNav3;
    private javax.swing.JButton CardsNav4;
    private javax.swing.JButton CardsNav5;
    private javax.swing.JButton CollectionNav;
    private javax.swing.JButton CollectionNav1;
    private javax.swing.JButton CollectionNav2;
    private javax.swing.JButton CollectionNav3;
    private javax.swing.JButton CollectionNav4;
    private javax.swing.JButton CollectionNav5;
    private javax.swing.JButton HomeNav;
    private javax.swing.JButton HomeNav1;
    private javax.swing.JButton HomeNav2;
    private javax.swing.JButton HomeNav3;
    private javax.swing.JButton HomeNav4;
    private javax.swing.JButton HomeNav5;
    private javax.swing.JButton LoginNav;
    private javax.swing.JButton LoginNav1;
    private javax.swing.JButton LoginNav2;
    private javax.swing.JButton LoginNav3;
    private javax.swing.JButton LoginNav4;
    private javax.swing.JButton LoginNav5;
    private javax.swing.JLabel LogoName;
    private javax.swing.JLabel LogoName1;
    private javax.swing.JLabel LogoName2;
    private javax.swing.JLabel LogoName3;
    private javax.swing.JLabel LogoName4;
    private javax.swing.JLabel LogoName5;
    private javax.swing.JLabel LogoName6;
    private javax.swing.JLabel LogoName7;
    private javax.swing.JPanel Navbar;
    private javax.swing.JPanel Navbar1;
    private javax.swing.JPanel Navbar2;
    private javax.swing.JPanel Navbar3;
    private javax.swing.JPanel Navbar4;
    private javax.swing.JPanel Navbar5;
    private javax.swing.JButton RegisterNav;
    private javax.swing.JTextField SearchNav;
    private javax.swing.JTextField SearchNav1;
    private javax.swing.JTextField SearchNav2;
    private javax.swing.JPanel ValuabelCard;
    private javax.swing.JPanel WelcomePanel;
    private javax.swing.JPanel WelcomePanel1;
    private javax.swing.JLabel WelcomeText;
    private javax.swing.JLabel WelcomeText1;
    private javax.swing.JLabel WelcomeText2;
    private javax.swing.JLabel WelcomeText3;
    private javax.swing.JPanel addCardPanel;
    private javax.swing.JButton addInv;
    private javax.swing.JButton addNewCard;
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton cancelAddBtn;
    private javax.swing.JButton cancelAddBtn1;
    private javax.swing.JPanel cardsGrid;
    private javax.swing.JScrollPane cardsScroll;
    private javax.swing.JPanel cardsShowPanel;
    private javax.swing.JPanel cgPanel;
    private javax.swing.JPanel collectionPanel;
    private javax.swing.JPanel collectiongrid;
    private javax.swing.JComboBox<String> conditionCB;
    private javax.swing.JComboBox<String> conditionCB1;
    private javax.swing.JPanel dashHomePanel;
    private javax.swing.JButton deleteCard;
    private javax.swing.JButton editCard;
    private javax.swing.JPanel editCardPanel;
    private javax.swing.JPanel featuredGrid;
    private javax.swing.JScrollPane featuredScroll;
    private javax.swing.JTextField idTF;
    private javax.swing.JTextField idTF1;
    private javax.swing.JTextField invSearchTF;
    private javax.swing.JPanel inventoryGrid;
    private javax.swing.JScrollPane inventoryScroll;
    private javax.swing.JPanel inventoryShow;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblImagePreview;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JButton logoutNav;
    private javax.swing.JButton logoutNav1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel mostRareCardLabel;
    private javax.swing.JTextField nameTF;
    private javax.swing.JTextField nameTF1;
    private javax.swing.JPanel parentPanel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTable pokeTable;
    private javax.swing.JPanel rareCards;
    private javax.swing.JComboBox<String> rarityCB;
    private javax.swing.JComboBox<String> rarityCB1;
    private javax.swing.JPanel recentCards;
    private javax.swing.JScrollPane recentScroll;
    private javax.swing.JButton refreshDashboard;
    private javax.swing.JPasswordField registerCPW;
    private javax.swing.JPasswordField registerPW;
    private javax.swing.JPanel registerPanel;
    private javax.swing.JTextField registerUN;
    private javax.swing.JButton removeSelectedBtn;
    private javax.swing.JButton removeSelectedBtn1;
    private javax.swing.JButton saveCardBtn;
    private javax.swing.JButton saveCardBtn1;
    private javax.swing.JButton searchBtn;
    private javax.swing.JButton searchBtncards;
    private javax.swing.JButton searchBtncards1;
    private javax.swing.JButton searchBtncards2;
    private javax.swing.JTextField searchTF;
    private javax.swing.JTextField searchTFcards;
    private javax.swing.JButton seeAllBtn;
    private javax.swing.JComboBox<String> sortCards1;
    private javax.swing.JComboBox<String> sortCardsCB;
    private javax.swing.JComboBox<String> sortingCB;
    private javax.swing.JPanel topCardGrid;
    private javax.swing.JScrollPane topScroll;
    private javax.swing.JPanel totalCards;
    private javax.swing.JLabel totalCardsLabel;
    private javax.swing.JPanel totalValue;
    private javax.swing.JLabel totalValueLabel;
    private javax.swing.JComboBox<String> typeCB;
    private javax.swing.JComboBox<String> typeCB1;
    private javax.swing.JButton undoDeleteBtn;
    private javax.swing.JPanel userCards;
    private javax.swing.JPanel userCollection;
    private javax.swing.JPanel userPanel;
    private javax.swing.JTextField userTF;
    private javax.swing.JLabel valueCardID;
    private javax.swing.JLabel valueCardName;
    private javax.swing.JLabel valueCardValue;
    private javax.swing.JTextField valueTF;
    private javax.swing.JTextField valueTF1;
    // End of variables declaration//GEN-END:variables
}