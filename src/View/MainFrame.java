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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
    private String selectedImagePath = "";
    private boolean isLoggedIn = false;
    private String currentUserRole = "";
    private String currentUsername = "";
    private String currentUser = "";
    private javax.swing.JList<String> recentList;
    private javax.swing.JScrollPane recentScrollPane;
    
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
        refreshDashboard();
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
            mostRareCardLabel.setText(String.format("Most Rare: %s (%s) - %s", 
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
        CardsNav2 = new javax.swing.JButton();
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
        CardsNav1 = new javax.swing.JButton();
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
        undoDelete = new javax.swing.JButton();
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
        userCollection = new javax.swing.JPanel();

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
                        .addContainerGap()
                        .addComponent(jLabel43))
                    .addGroup(rareCardsLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(mostRareCardLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        dashHomePanel.add(rareCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 410, 210, 90));

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
                .addContainerGap(53, Short.MAX_VALUE))
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

        dashHomePanel.add(totalCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 410, 260, 90));

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

        dashHomePanel.add(totalValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 410, 160, 90));

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

        CardsNav2.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CardsNav2.setText("Cards");
        CardsNav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardsNav2ActionPerformed(evt);
            }
        });

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
                .addGap(18, 18, 18)
                .addComponent(CardsNav2)
                .addGap(18, 18, 18)
                .addComponent(CollectionNav2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
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
                        .addComponent(CardsNav2)
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

        CardsNav1.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 14)); // NOI18N
        CardsNav1.setText("Cards");
        CardsNav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CardsNav1ActionPerformed(evt);
            }
        });

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
                .addGap(18, 18, 18)
                .addComponent(CardsNav1)
                .addGap(18, 18, 18)
                .addComponent(CollectionNav1)
                .addGap(33, 33, 33)
                .addComponent(searchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(logoutNav1)
                .addGap(37, 37, 37))
        );
        Navbar1Layout.setVerticalGroup(
            Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Navbar1Layout.createSequentialGroup()
                .addGroup(Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Navbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LogoName3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CardsNav1)
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

        undoDelete.setFont(new java.awt.Font("Bauhaus 93", 0, 12)); // NOI18N
        undoDelete.setText("Undo Delete");
        undoDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoDeleteActionPerformed(evt);
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
                                .addComponent(undoDelete)
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
                    .addComponent(undoDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        userPanel.add(seeAllBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 220, -1, -1));

        topScroll.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new javax.swing.ImageIcon(getClass().getResource("/utils/blastoise.png")))); // NOI18N

        topCardGrid.setLayout(new java.awt.GridLayout(2, 3, 25, 25));
        topScroll.setViewportView(topCardGrid);

        userPanel.add(topScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 252, 960, 290));

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/utils/watah.jpg"))); // NOI18N
        jLabel38.setPreferredSize(new java.awt.Dimension(980, 550));
        userPanel.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, -1));

        parentPanel.add(userPanel, "card7");

        javax.swing.GroupLayout userCardsLayout = new javax.swing.GroupLayout(userCards);
        userCards.setLayout(userCardsLayout);
        userCardsLayout.setHorizontalGroup(
            userCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
        );
        userCardsLayout.setVerticalGroup(
            userCardsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );

        parentPanel.add(userCards, "card8");

        javax.swing.GroupLayout userCollectionLayout = new javax.swing.GroupLayout(userCollection);
        userCollection.setLayout(userCollectionLayout);
        userCollectionLayout.setHorizontalGroup(
            userCollectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
        );
        userCollectionLayout.setVerticalGroup(
            userCollectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );

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
            User currentUser = user.getUserByUsername(username);

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

    private void CardsNav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardsNav1ActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        JOptionPane.showMessageDialog(this, 
            "We are sorry, this function is not added yet", 
            "-PokeMuseum", 
            JOptionPane.INFORMATION_MESSAGE,
            pokeIcon);
    }//GEN-LAST:event_CardsNav1ActionPerformed

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

    private void CardsNav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CardsNav2ActionPerformed
        ImageIcon pokeIcon = new ImageIcon(getClass().getResource("/utils/PokeLogo.png"));
        JOptionPane.showMessageDialog(this, 
            "We are sorry, this function is not added yet", 
            "-PokeMuseum", 
            JOptionPane.INFORMATION_MESSAGE,
            pokeIcon);
    }//GEN-LAST:event_CardsNav2ActionPerformed

    private void CollectionNav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav2ActionPerformed
        parentPanel.removeAll();
        parentPanel.add(collectionPanel);
        parentPanel.repaint();
        parentPanel.revalidate();
        
        refreshCardTable(controller.readAllCards());
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

    private void undoDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_undoDeleteActionPerformed

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

        // Step 1: Try exact ID match first (fastest + most specific)
        PokeCard byId = controller.searchByIdHash(query);
        if (byId != null) {
            results.add(byId);
        } 
        // Step 2: If no ID match → do name search (partial)
        else {
            results = controller.searchByNameLinear(query);
        }

        // Optional: Try value if it's a number and no results yet
        if (results.isEmpty()) {
            try {
                double val = Double.parseDouble(query);
                PokeCard byValue = controller.searchByValueBinary(val);
                if (byValue != null) {
                    results.add(byValue);
                }
            } catch (NumberFormatException ignored) {
            // not a number → ignore
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
        // TODO add your handling code here:
    }//GEN-LAST:event_CardsNav3ActionPerformed

    private void CollectionNav3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CollectionNav3ActionPerformed
        // TODO add your handling code here:
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
    private javax.swing.JButton CardsNav1;
    private javax.swing.JButton CardsNav2;
    private javax.swing.JButton CardsNav3;
    private javax.swing.JButton CollectionNav;
    private javax.swing.JButton CollectionNav1;
    private javax.swing.JButton CollectionNav2;
    private javax.swing.JButton CollectionNav3;
    private javax.swing.JButton HomeNav;
    private javax.swing.JButton HomeNav1;
    private javax.swing.JButton HomeNav2;
    private javax.swing.JButton HomeNav3;
    private javax.swing.JButton LoginNav;
    private javax.swing.JButton LoginNav1;
    private javax.swing.JButton LoginNav2;
    private javax.swing.JButton LoginNav3;
    private javax.swing.JLabel LogoName;
    private javax.swing.JLabel LogoName1;
    private javax.swing.JLabel LogoName2;
    private javax.swing.JLabel LogoName3;
    private javax.swing.JLabel LogoName4;
    private javax.swing.JLabel LogoName5;
    private javax.swing.JPanel Navbar;
    private javax.swing.JPanel Navbar1;
    private javax.swing.JPanel Navbar2;
    private javax.swing.JPanel Navbar3;
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
    private javax.swing.JButton addNewCard;
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton cancelAddBtn;
    private javax.swing.JButton cancelAddBtn1;
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
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JButton saveCardBtn;
    private javax.swing.JButton saveCardBtn1;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchTF;
    private javax.swing.JButton seeAllBtn;
    private javax.swing.JComboBox<String> sortingCB;
    private javax.swing.JPanel topCardGrid;
    private javax.swing.JScrollPane topScroll;
    private javax.swing.JPanel totalCards;
    private javax.swing.JLabel totalCardsLabel;
    private javax.swing.JPanel totalValue;
    private javax.swing.JLabel totalValueLabel;
    private javax.swing.JComboBox<String> typeCB;
    private javax.swing.JComboBox<String> typeCB1;
    private javax.swing.JButton undoDelete;
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