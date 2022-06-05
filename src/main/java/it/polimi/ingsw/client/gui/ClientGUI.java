package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.gui.component.CharacterSelectorPanel;
import it.polimi.ingsw.client.gui.component.EntranceStudentSelectorPanel;
import it.polimi.ingsw.client.gui.component.StudentSelectorByColor;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.impl.Character1;
import it.polimi.ingsw.model.character.impl.Character10;
import it.polimi.ingsw.model.character.impl.Character11;
import it.polimi.ingsw.model.character.impl.Character7;
import it.polimi.ingsw.protocol.Message;
import it.polimi.ingsw.protocol.message.*;
import it.polimi.ingsw.protocol.message.character.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class ClientGUI extends Client {
    private JFrame frame;
    private JPanel questionsPanel;
    private JLabel errorLabel;
    private ViewGUI view;

    public JFrame getFrame() {
        return frame;
    }

    @Override
    public void run() throws IOException {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Eriantys");

            JPanel contentPane = new JPanel();
            contentPane.setOpaque(true);
            contentPane.setBackground(Color.WHITE);
            contentPane.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

            questionsPanel = new JPanel();
            questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
            questionsPanel.setMaximumSize(new Dimension(320, 90));
            questionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPane.add(questionsPanel);

            errorLabel = new JLabel();
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            frame.setContentPane(contentPane);

            JLabel titleLbl = new JLabel("Insert IP address and port");
            titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionsPanel.add(titleLbl);
            JTextField ipField = new JTextField();
            ipField.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionsPanel.add(ipField);
            JTextField portField = new JTextField("61863");
            portField.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionsPanel.add(portField);
            JButton confirm = new JButton("Connect");
            confirm.setAlignmentX(Component.CENTER_ALIGNMENT);
            confirm.addActionListener(actionEvent -> {
                int port;
                try {
                    port = Integer.parseInt(portField.getText());
                } catch (NumberFormatException e) {
                    errorLabel.setText("Invalid port");
                    frame.revalidate();
                    frame.repaint();
                    return;
                }
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ipField.getText(), port), 5000);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    errorLabel.setText("");
                    Thread t = new Thread(new ClientSocketWorker(this));
                    t.start();
                } catch (IOException e) {
                    errorLabel.setText("Connection failed: " + e.getMessage());
                    frame.revalidate();
                    frame.repaint();
                }
            });
            questionsPanel.add(errorLabel);
            questionsPanel.add(confirm);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    public void processMessage(Message msg) {
        //We have received a server message, check its Type to answer appropriately
        switch (msg.getMessageId()) {
            case ERROR -> {
                errorLabel.setText("Server error: " + ((ErrorMessage) msg).getError());
                frame.revalidate();
                frame.repaint();
            }
            case ASK_USERNAME -> {
                questionsPanel.removeAll();
                JLabel titleLbl = new JLabel("Insert username");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                questionsPanel.add(titleLbl);
                JTextField usernameTextField = new JTextField();
                usernameTextField.setPreferredSize(new Dimension(320, 40));
                usernameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
                usernameTextField.addActionListener(actionEvent -> {
                    if (usernameTextField.getText().isEmpty()) {
                        return;
                    }
                    questionsPanel.removeAll();
                    JLabel waitLbl = new JLabel("Waiting for other players...");
                    waitLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                    questionsPanel.add(waitLbl);
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    name = usernameTextField.getText();
                    sendMessageAsync(new SetUsernameMessage(usernameTextField.getText()));
                });
                questionsPanel.add(usernameTextField);
                questionsPanel.add(errorLabel);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_PLAYER_NUMBER -> {
                questionsPanel.removeAll(); //Remove the "Waiting for other players..." JLabel
                JLabel titleLbl = new JLabel("Choose player number");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                questionsPanel.add(titleLbl);
                JComboBox<Integer> comboBox = new JComboBox<>(new Integer[]{2, 3, 4});
                comboBox.setMaximumSize(new Dimension(320, 40));
                comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
                questionsPanel.add(comboBox);
                JButton confirmPlayers = new JButton("Confirm");
                confirmPlayers.setMaximumSize(new Dimension(320, 40));
                confirmPlayers.setAlignmentX(Component.CENTER_ALIGNMENT);
                confirmPlayers.addActionListener(actionEvent -> {
                    questionsPanel.removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new SetPlayerNumberMessage((Integer) comboBox.getSelectedItem()));
                });
                questionsPanel.add(errorLabel);
                questionsPanel.add(confirmPlayers);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_EXPERT -> {
                JLabel titleLbl = new JLabel("Activate expert mode?");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                questionsPanel.add(titleLbl);
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"Yes", "No"});
                comboBox.setMaximumSize(new Dimension(320, 40));
                comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
                questionsPanel.add(comboBox);
                JButton confirmExpert = new JButton("Confirm");
                confirmExpert.setMaximumSize(new Dimension(320, 40));
                confirmExpert.setAlignmentX(Component.CENTER_ALIGNMENT);
                confirmExpert.addActionListener(actionEvent -> {
                    questionsPanel.removeAll();
                    JLabel waitLbl = new JLabel("Waiting for other players...");
                    waitLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                    questionsPanel.add(waitLbl);
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new SetExpertMessage(comboBox.getSelectedItem().equals("Yes")));
                });
                questionsPanel.add(errorLabel);
                questionsPanel.add(confirmExpert);
                frame.revalidate();
                frame.repaint();
            }
            case UPDATE_VIEW -> {
                questionsPanel = null;
                if (view == null) {
                    view = ViewGUI.create(this, ((UpdateViewMessage) msg).getPlayersOrder().size());
                }
                view.handleUpdateView((UpdateViewMessage) msg);
            }
            case ASK_ASSISTANT -> {
                JLabel titleLbl = new JLabel("Choose an assistant");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                view.getBottomPanel().add(titleLbl);

                JPanel assistPanel = new JPanel();
                assistPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JComboBox<Integer> comboBox = new JComboBox<>(view.getAssistants().stream().map(Assistant::getValue).toArray(Integer[]::new));
                assistPanel.add(comboBox);
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new SetAssistantMessage((Integer) comboBox.getSelectedItem()));
                });
                assistPanel.add(confirm);
                view.getBottomPanel().add(assistPanel);
                view.getBottomPanel().add(errorLabel);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_ENTRANCE_STUDENT -> {
                Player player = view.getPlayerFromName(name);

                JLabel titleLbl = new JLabel("Move 3 entrance students");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                view.getBottomPanel().add(titleLbl);

                int i = 0;
                JPanel selectorsPanel = new JPanel();
                selectorsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                EntranceStudentSelectorPanel[] studentSelectors = new EntranceStudentSelectorPanel[player.getSchool().getEntrance().size()];
                for (Student student : player.getSchool().getEntrance()) {
                    selectorsPanel.add(studentSelectors[i++] = new EntranceStudentSelectorPanel(student, view.getIslands()));
                }
                view.getBottomPanel().add(selectorsPanel);

                JButton confirm = new JButton("Confirm");
                confirm.setAlignmentX(Component.CENTER_ALIGNMENT);
                confirm.addActionListener(actionEvent -> {
                    Map<Integer, Map<PawnColor, Integer>> islandsStudents = new HashMap<>();
                    Map<PawnColor, Integer> tableStudents = new HashMap<>();
                    int totalStudents = 0;
                    for (EntranceStudentSelectorPanel pan : studentSelectors) {
                        if (pan.getSelection() == EntranceStudentSelectorPanel.SELECTION_TABLE) {
                            ++totalStudents;
                            tableStudents.put(pan.getStudent().getColor(), tableStudents.getOrDefault(pan.getStudent().getColor(), 0) + 1);
                        } else if (pan.getSelection() >= 0) {
                            ++totalStudents;
                            if (!islandsStudents.containsKey(pan.getSelection())) {
                                islandsStudents.put(pan.getSelection(), new HashMap<>());
                            }
                            islandsStudents.get(pan.getSelection()).put(pan.getStudent().getColor(), islandsStudents.get(pan.getSelection()).getOrDefault(pan.getStudent().getColor(), 0) + 1);
                        }
                    }
                    if (totalStudents == 3) {
                        view.getBottomPanel().removeAll();
                        errorLabel.setText("");
                        frame.revalidate();
                        frame.repaint();
                        sendMessageAsync(new SetEntranceStudentMessage(islandsStudents, tableStudents));
                    } else {
                        errorLabel.setText("You must select exactly three students");
                        frame.revalidate();
                        frame.repaint();
                    }
                });

                view.getBottomPanel().add(errorLabel);
                view.getBottomPanel().add(confirm);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_MOTHER_NATURE -> {
                JLabel titleLbl = new JLabel("Move mother nature");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                view.getBottomPanel().add(titleLbl);

                JPanel movesPanel = new JPanel();
                movesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JComboBox<Integer> comboBox = new JComboBox<>();
                Player player = view.getPlayerFromName(name);
                for (int i = 1; i <= player.getCurrentAssistant().getMoves() + player.getAdditionalMoves(); ++i) {
                    comboBox.addItem(i);
                }
                movesPanel.add(comboBox);
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new SetMotherNatureMessage((Integer) comboBox.getSelectedItem()));
                });
                movesPanel.add(confirm);
                view.getBottomPanel().add(movesPanel);
                view.getBottomPanel().add(errorLabel);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_CLOUD -> {
                JLabel titleLbl = new JLabel("Choose a cloud");
                titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                view.getBottomPanel().add(titleLbl);

                JPanel cloudPanel = new JPanel();
                cloudPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JComboBox<Integer> comboBox = new JComboBox<>();
                for (int i = 1; i <= view.getClouds().size(); ++i) {
                    comboBox.addItem(i);
                }
                cloudPanel.add(comboBox);
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new SetCloudMessage((Integer) comboBox.getSelectedItem() - 1));
                });
                cloudPanel.add(confirm);
                view.getBottomPanel().add(cloudPanel);
                view.getBottomPanel().add(errorLabel);
                frame.revalidate();
                frame.repaint();
            }
            case ASK_CHARACTER -> {
                AskCharacterMessage characterMessage = (AskCharacterMessage) msg;
                if (characterMessage.getCharacterId() == -1) {
                    handleCharacter();
                } else {
                    askCharacterParameters(characterMessage.getCharacterId());
                }
            }
        }
    }

    private void handleCharacter() {
        Player player = view.getPlayerFromName(name);
        int coins = player.getCoins();
        List<Character> usableCharacters = new ArrayList<>(view.getCharacters().stream().filter(c -> coins >= c.getCost()).toList());
        if (player.getSchool().getTables().values().stream().mapToInt(List::size).sum() == 0) {
            //If the table is empty, we can't play Character10, so remove it from usableCharacters
            usableCharacters.removeIf(c -> c instanceof Character10);
        }
        if (view.isExpert() && !usableCharacters.isEmpty()) {
            JLabel titleLbl = new JLabel("Choose a character");
            titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            view.getBottomPanel().add(titleLbl);

            JPanel charPanel = new JPanel();
            charPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            CharacterSelectorPanel characterSelectorPanel = new CharacterSelectorPanel(usableCharacters);
            charPanel.add(characterSelectorPanel);

            JButton confirm = new JButton("Confirm");
            confirm.addActionListener(actionEvent -> {
                view.getBottomPanel().removeAll();
                errorLabel.setText("");
                frame.revalidate();
                frame.repaint();
                int sel = characterSelectorPanel.getSelection();
                if (sel == CharacterSelectorPanel.SELECTION_NONE) {
                    sendMessageAsync(new UseNoCharacterMessage());
                } else {
                    askCharacterParameters(view.getCharacters().get(sel));
                }
            });
            charPanel.add(confirm);
            view.getBottomPanel().add(charPanel);
            frame.revalidate();
            frame.repaint();
        } else {
            sendMessageAsync(new UseNoCharacterMessage());
        }
    }

    private void askCharacterParameters(int characterId) {
        Character c = null;
        for (Character character : view.getCharacters()) {
            if (character.getId() == characterId) {
                c = character;
                break;
            }
        }
        askCharacterParameters(c);
    }

    private void askCharacterParameters(Character c) {
        JLabel titleLbl = new JLabel();
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel paramsPanel = new JPanel();
        paramsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        switch (c.getId()) {
            case 0 -> {
                Character1 c1 = (Character1) c;
                titleLbl.setText("Choose a student to move from the character card and an island");

                JComboBox<PawnColor> comboBox = new JComboBox<>(c1.getStudents().stream().map(Student::getColor).distinct().toArray(PawnColor[]::new));
                paramsPanel.add(comboBox);

                JComboBox<String> islandsComboBox = new JComboBox<>();
                for (int i = 0; i < view.getIslands().size(); ++i) {
                    islandsComboBox.addItem("Island " + (i + 1));
                }
                paramsPanel.add(islandsComboBox);

                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    int islandIndex = Integer.parseInt(((String) islandsComboBox.getSelectedItem()).split(" ")[1]) - 1;
                    sendMessageAsync(new UseCharacterColorIslandMessage((PawnColor) comboBox.getSelectedItem(), islandIndex));
                });
                paramsPanel.add(confirm);
            }
            case 1,3,5,7 -> {
                view.getBottomPanel().removeAll();
                errorLabel.setText("");
                frame.revalidate();
                frame.repaint();
                sendMessageAsync(new UseCharacterMessage(c.getId()));
                return;
            }
            case 2,4 -> {
                titleLbl.setText("Choose an island");
                JComboBox<String> islandsComboBox = new JComboBox<>();
                for (int i = 0; i < view.getIslands().size(); ++i) {
                    islandsComboBox.addItem("Island " + (i + 1));
                }
                paramsPanel.add(islandsComboBox);
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    int islandIndex = Integer.parseInt(((String) islandsComboBox.getSelectedItem()).split(" ")[1]) - 1;
                    sendMessageAsync(new UseCharacterIslandMessage(c.getId(), islandIndex));
                });
                paramsPanel.add(confirm);
            }
            case 6 -> {
                Character7 c7 = (Character7) c;
                titleLbl.setText("Select up to 3 entrance students and character students to exchange");

                JPanel selPanel = new JPanel(null);
                selPanel.setLayout(new BoxLayout(selPanel, BoxLayout.Y_AXIS));
                StudentSelectorByColor entranceSel = new StudentSelectorByColor(view.getPlayerFromName(name).getSchool().getEntrance(), 3, "Entrance: ");
                selPanel.add(entranceSel);
                StudentSelectorByColor characterSel = new StudentSelectorByColor(c7.getStudents(), 3, "Character: ");
                selPanel.add(characterSel);

                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    Map<PawnColor, Integer> entranceToCardMap = entranceSel.getSelection();
                    Map<PawnColor, Integer> cardToEntranceMap = characterSel.getSelection();
                    if (entranceToCardMap.size() != cardToEntranceMap.size()) {
                        errorLabel.setText("You must select the same number of students from entrance and card");
                        frame.revalidate();
                        frame.repaint();
                    } else if (entranceToCardMap.size() < 1 || entranceToCardMap.size() > 3) {
                        errorLabel.setText("You may only select up to 3 students");
                        frame.revalidate();
                        frame.repaint();
                    } else {
                        view.getBottomPanel().removeAll();
                        errorLabel.setText("");
                        frame.revalidate();
                        frame.repaint();
                        sendMessageAsync(new UseCharacterStudentMapMessage(c.getId(), entranceToCardMap, cardToEntranceMap));
                    }
                });
                selPanel.add(confirm);
                paramsPanel.add(selPanel);
            }
            case 8,11 -> {
                titleLbl.setText("Choose a student color");
                JComboBox<PawnColor> comboBox = new JComboBox<>(PawnColor.values());
                paramsPanel.add(comboBox);
                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new UseCharacterColorMessage(c.getId(), (PawnColor) comboBox.getSelectedItem()));
                });
                paramsPanel.add(confirm);
            }
            case 9 -> {
                titleLbl.setText("Select up to 2 entrance students and table students to exchange");

                JPanel selPanel = new JPanel(null);
                selPanel.setLayout(new BoxLayout(selPanel, BoxLayout.Y_AXIS));
                StudentSelectorByColor entranceSel = new StudentSelectorByColor(view.getPlayerFromName(name).getSchool().getEntrance(), 2, "Entrance: ");
                selPanel.add(entranceSel);
                StudentSelectorByColor tableSel = new StudentSelectorByColor(view.getPlayerFromName(name).getSchool().getTables().values().stream().flatMap(Collection::stream).toList(), 2, "Table: ");
                selPanel.add(tableSel);

                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    Map<PawnColor, Integer> entranceToCardMap = entranceSel.getSelection();
                    Map<PawnColor, Integer> cardToEntranceMap = tableSel.getSelection();
                    if (entranceToCardMap.size() != cardToEntranceMap.size()) {
                        errorLabel.setText("You must select the same number of students from entrance and card");
                        frame.revalidate();
                        frame.repaint();
                    } else if (entranceToCardMap.size() < 1 || entranceToCardMap.size() > 2) {
                        errorLabel.setText("You may only select up to 3 students");
                        frame.revalidate();
                        frame.repaint();
                    } else {
                        view.getBottomPanel().removeAll();
                        errorLabel.setText("");
                        frame.revalidate();
                        frame.repaint();
                        sendMessageAsync(new UseCharacterStudentMapMessage(c.getId(), entranceToCardMap, cardToEntranceMap));
                    }
                });
                selPanel.add(confirm);
                paramsPanel.add(selPanel);
            }
            case 10 -> {
                Character11 c11 = (Character11) c;
                titleLbl.setText("Choose a student to move from the character card to your dining room");

                JComboBox<PawnColor> comboBox = new JComboBox<>(c11.getStudents().stream().map(Student::getColor).distinct().toArray(PawnColor[]::new));
                paramsPanel.add(comboBox);

                JButton confirm = new JButton("Confirm");
                confirm.addActionListener(actionEvent -> {
                    view.getBottomPanel().removeAll();
                    errorLabel.setText("");
                    frame.revalidate();
                    frame.repaint();
                    sendMessageAsync(new UseCharacterColorMessage(c.getId(), (PawnColor) comboBox.getSelectedItem()));
                });
                paramsPanel.add(confirm);
            }
        }
        view.getBottomPanel().add(titleLbl);
        view.getBottomPanel().add(paramsPanel);
        view.getBottomPanel().add(errorLabel);
        frame.revalidate();
        frame.repaint();
    }
}
