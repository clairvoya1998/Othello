import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class OthelloFrame extends JFrame {
    public JPanel board;
    JButton humanVcomp = new JButton("Human V. Computer");
    JButton humanVhuman = new JButton("Human V. Human");
    public static boolean AI = false;

    public static void main(String[] args) {
        Reversi.getInstance().newGame();
        EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                    }

                    /* creation of JFrame */
                    OthelloFrame frame = new OthelloFrame();
                    frame.setSize(700,760);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });

    }

   /* ActionListeners to give functionality to JButtons in JPanel */
    public class hVcListener implements ActionListener{ // Human versus computer, implement AI
      public void actionPerformed(ActionEvent event){
          System.out.println("HUMAN VS. COMPUTER");
          AI = true;
          System.out.println(AI);
      }
   }
    public class hVhListener implements ActionListener{ // two player game
        public void actionPerformed(ActionEvent event){
          System.out.println("HUMAN VS.HUMAN");
          AI = false;
          System.out.println(AI);
        }
    }
    public class exitListener implements ActionListener{ // exit game
        public void actionPerformed(ActionEvent event){
          System.out.println("Game Over.");
          System.exit(0);
          }
    }
    public class newGameListener implements ActionListener{ // new game
      public void actionPerformed(ActionEvent event){
          Reversi.getInstance().newGame();
          boolean check = true;
          add(board = new Board(check), BorderLayout.CENTER);
      }
    }

    public OthelloFrame() {

        JPanel topPanel = new JPanel(); // creation of JPanel
        topPanel.setLayout(new FlowLayout());

        add(topPanel, BorderLayout.NORTH);
        add(board = new Board(false), BorderLayout.CENTER); // add Board to JPanel

        JButton hVc = new JButton("Human V. Computer");
        JButton hVh = new JButton("Human V. Human");
        JButton exit = new JButton("Exit Game");
        JButton newGame = new JButton("New Game");

        /* adding buttons to JPanel */
        topPanel.add(hVc);
        topPanel.add(hVh);
        topPanel.add(exit);
        topPanel.add(newGame);

        /* adding functionality to buttons */
        ActionListener listener = new hVcListener();
        hVc.addActionListener(listener);
        ActionListener listener1 = new hVhListener();
        hVh.addActionListener(listener1);
        ActionListener listener2 = new exitListener();
        exit.addActionListener(listener2);
        ActionListener listener3 = new newGameListener();
        newGame.addActionListener(listener3);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    private class Board extends JPanel{

        /* constants for board*/
        private static final int BOARD_SIZE = 8; //board size 8x8
        private static final int SQUARE_SIZE = 80; //square size 80x80 pixels
        private static final int BOARD_SIZE_PIXEL = 640; //board size 640x640 pixels
        private static final int BORDER_SIZE = 3; //boarder width

        private final Color BOARD_COLOR = new Color(110, 245, 150);
        private final char[][] board =  Reversi.getInstance().getBoard();

        private final Font FONT = new Font("Impact", Font.BOLD, 16); //side text
        private final Color TEXT_COLOR = Color.black;
        private final String[] BOARD_ROW_TEXT = {"A", "B", "C", "D", "E", "F", "G", "H"};
        private final String[] BOARD_COL_TEXT = {"1", "2", "3", "4", "5", "6", "7", "8"};

        private Rectangle selectedCell = null;

        //graphics/pictures for board
        public BufferedImage blackPieceImg =  null;
        public BufferedImage whitePieceImg = null;
        public BufferedImage gameboard = null;

        public String themeChoice = null;


        public Board(boolean check) {
              /* picking theme of the board */
            if (!check) {
                System.out.println("Which theme would you like? ");  // if the board a new game ask for theme choice
                String[] Theme = {"Default", "Tropical", "Surprise", "Galaxy"};
                for (int i = 0; i < Theme.length; i++) {
                    System.out.println(Theme[i]);
                }
                System.out.print("Pick a theme: ");
                themeChoice = EasyIn.getString();

                try {
                    blackPieceImg = ImageIO.read(new File("blackpiece.png")); //black game piece
                    whitePieceImg = ImageIO.read(new File("whitepiece.png")); //white game piece
                    if (themeChoice.equals("Galaxy")) {
                        gameboard = ImageIO.read(new File("galaxyBoard.jpg")); //galaxy theme
                    } else if (themeChoice.equals("Tropical")) {
                        gameboard = ImageIO.read(new File("tropicalBoard.jpg")); //tropical theme
                    } else if (themeChoice.equals("Surprise")) {
                        gameboard = ImageIO.read(new File("surpriseBoard.png")); //surprise theme
                    } else {
                        if (!themeChoice.equals("Default")) { // will set to default if the user's theme choice is not valid
                            System.out.println("Did not recognize theme choice, setting board to theme: Default.");
                        }
                        gameboard = ImageIO.read(new File("defaultBoard.jpg"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            addMouseListener(new MouseAdapter() { //mouse listener for mouse events on JPanel
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedCell = null;
                        /* creation of rectangles that overlay board
                          used so that mouse events work for individual rectangles*/
                        for (int col = 0; col < BOARD_SIZE && selectedCell == null; col++) {
                            for (int row = 0; row < BOARD_SIZE; row++) {
                                int x = (BOARD_SIZE_PIXEL / BOARD_SIZE) * col;
                                int y = (BOARD_SIZE_PIXEL / BOARD_SIZE) * row;
                                Rectangle cell = new Rectangle(x, y, BOARD_SIZE_PIXEL / BOARD_SIZE, BOARD_SIZE_PIXEL / BOARD_SIZE);
                                if (cell.contains(e.getPoint())) { // if where mouse event occurs run movePiece
                                    selectedCell = cell;
                                    Reversi.getInstance().movePiece(board, col, row, OthelloFrame.AI);
                                    repaint(); //repaint board (update)
                                    break;
                                }
                            }
                        }
                    }

                });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.drawImage(gameboard, 0, 0, BOARD_SIZE_PIXEL, BOARD_SIZE_PIXEL, null);

            //grid
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(BORDER_SIZE));
            for(int i = 0; i < BOARD_SIZE; i++){
                g.setColor(Color.black);
                g.drawLine(0, i*SQUARE_SIZE, BOARD_SIZE_PIXEL, i*SQUARE_SIZE);
                g.drawLine(i*SQUARE_SIZE, 0, i*SQUARE_SIZE, BOARD_SIZE_PIXEL);
            }
            //border
            g.setColor(Color.black);
            g.fillRect(0,0,BORDER_SIZE,BOARD_SIZE_PIXEL);
            g.fillRect(0,0,BOARD_SIZE_PIXEL,BORDER_SIZE);
            g.fillRect(0,BOARD_SIZE_PIXEL,BOARD_SIZE_PIXEL ,BORDER_SIZE);
            g.fillRect(BOARD_SIZE_PIXEL,0,BORDER_SIZE,BOARD_SIZE_PIXEL  + BORDER_SIZE);


            //text
            g.setColor(TEXT_COLOR);
            g.setFont(FONT);
            for (int i = 0; i < BOARD_COL_TEXT.length; i++)
            {
                g.drawString(BOARD_COL_TEXT[i], 40 + i*SQUARE_SIZE , 670);
                g.drawString(BOARD_ROW_TEXT [i], 660, 40 + i*SQUARE_SIZE);
            }

            //intial game pieces
            //Reversi.getInstance().newGame();

            //moves
            for (int r = 0; r < BOARD_SIZE; r++){
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (board[r][c] == Reversi.BLACK_PIECE) {
                        //g.setColor(Color.black);
                        drawPiece(g, blackPieceImg, r, c);
                    }
                    if (board[r][c] == Reversi.WHITE_PIECE) {
                        //g.setColor(Color.white);
                        drawPiece(g, whitePieceImg, r, c);
                    }
                }
            }
        }

        //draw game piece
        private void drawPiece(Graphics g, BufferedImage img, int row, int col) {
            g.drawImage(img, row * SQUARE_SIZE ,col * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, null);
        }

    }
}
