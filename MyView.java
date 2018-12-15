import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class MyView extends JFrame implements ActionListener {


    private final int WIDTH=1000;
    private final int HEIGHT=500;

    private JList listView;
    private DefaultListModel<Telephone> listModel = new DefaultListModel();
    private Map<Object,Telephone> indexMap=new TreeMap<>();
    private Map<Object,Telephone> reversedMap=new TreeMap<>(Collections.reverseOrder());;
    private String selectedField;
    private boolean ascendingSorting=true;
    private String filePath;
    private int mode=0;
    private  JComboBox<String> comboBox ;

    private void createListModel( Map<Object,Telephone> indexMap)
    {
        this.listModel.removeAllElements();
        for(Telephone i : indexMap.values())
        {
            if(!i.isDeleted())
                listModel.addElement(i);
        }
    }



    public MyView() throws HeadlessException {

        JFrame self = this;
        self.setVisible(true);
        self.setTitle("Telephone list");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        self.setBounds(dimension.width / 6, dimension.height / 5, WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem save = new JMenuItem("Save");

        fileMenu.add(load);
        fileMenu.add(save);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel pTop = new JPanel();
        JPanel pLeft = new JPanel();
        JPanel pCenter = new JPanel();
        JPanel pProperties = new JPanel();

        pTop.add(new JLabel("Sorting parameter : "));
        comboBox = new JComboBox<>();
        comboBox.addItem("rate");
        comboBox.addItem("name");
        comboBox.addItem("surname");
        comboBox.addItem("patronymic");
        comboBox.addItem("number");
        selectedField = comboBox.getSelectedItem().toString();
        comboBox.addActionListener(this);
        pTop.add(comboBox);

        JRadioButton sortAscending = new JRadioButton("Sort ascending");
        JRadioButton sortDescending = new JRadioButton("Sort descending");
        sortAscending.setSelected(true);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(sortAscending);
        buttonGroup.add(sortDescending);
        pTop.add(sortAscending);
        pTop.add(sortDescending);


        pLeft.setLayout(new GridLayout(15, 2));
        JTextField key = new JTextField();
        pLeft.add(new JLabel("Key"));
        pLeft.add(key);
        JButton remove = new JButton("Remove by Key");
        pLeft.add(remove);
        JButton find = new JButton("Find");
        pLeft.add(find);
        JRadioButton byKey = new JRadioButton("By key");
        JRadioButton moreThanKey = new JRadioButton("> key");
        JRadioButton lessThanKey = new JRadioButton("< key");
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(byKey);
        buttonGroup2.add(moreThanKey);
        buttonGroup2.add(lessThanKey);
        pLeft.add(byKey);
        pLeft.add(moreThanKey);
        pLeft.add(lessThanKey);

        Border etched = BorderFactory.createEtchedBorder();
        Border titled = BorderFactory.createTitledBorder(etched, "Add new");
        pProperties.setBounds(dimension.width / 3 * 2, dimension.height / 3 * 2, dimension.width / 3 / 3 * 2, dimension.width / 3 / 3 * 2);
        pProperties.setBorder(titled);

        pProperties.setLayout(new GridLayout(15, 2));

        JTextField name = new JTextField();
        pProperties.add(new JLabel("Name"));
        pProperties.add(name);


        JTextField surname = new JTextField(dimension.width / 50);
        pProperties.add(new JLabel("Surname"));
        pProperties.add(surname);


        JTextField patronymic = new JTextField();
        pProperties.add(new JLabel("patronymic"));
        pProperties.add(patronymic);


        JTextField number = new JTextField();
        pProperties.add(new JLabel("number"));
        pProperties.add(number);

        JTextField rate = new JTextField();
        pProperties.add(new JLabel("rate"));
        pProperties.add(rate);

        JButton submit = new JButton("Submit");
        pProperties.add(submit);


        listView=new JList(listModel);
        self.add(listView, BorderLayout.CENTER);
        self.add(pTop, BorderLayout.NORTH);
        self.add(pLeft, BorderLayout.WEST);
        self.add(new Label("Project Loaded Successfully!"), BorderLayout.SOUTH);
        self.add(pProperties, BorderLayout.EAST);


        submit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sName = name.getText();
                String sSurname = surname.getText();
                String sPatronymic = patronymic.getText();
                String sNumber = number.getText();
                int sRate = Integer.parseInt(rate.getText());
                Telephone telephone = new Telephone(sName, sSurname, sPatronymic, sNumber, sRate);

                try {
                    indexMap.put(telephone.getFieldName(selectedField),telephone);
                    reversedMap.putAll(indexMap);
                    if(ascendingSorting){
                        createListModel(indexMap);
                    }
                    else{
                        createListModel(reversedMap);
                    }

                } catch (IllegalAccessException | NoSuchFieldException e1) {
                    e1.printStackTrace();
                }

            }
        });

        sortAscending.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createListModel(indexMap);
                ascendingSorting=true;
            }
        });

        sortDescending.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createListModel(reversedMap);
                ascendingSorting=false;
            }
        });


        remove.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strKey = key.getText();
                boolean isFound=false;
                Field field = null;
                try {

                    field = Telephone.class.getDeclaredField(selectedField);
                    field.setAccessible(true);
                    for(Telephone i:indexMap.values())
                    {
                        if (field.get(i).toString().equals(strKey)) {
                            i.setDeleted();
                            listModel.removeElement(i);
                            isFound=true;
                        }
                    }
                    if(!isFound)
                    {
                        JOptionPane.showMessageDialog(self,"No such object");
                    }
                } catch (NoSuchFieldException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
        });

        byKey.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode=1;
            }});

        moreThanKey.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode=2;
            }});


        lessThanKey.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode=3;
            }});


        find.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mode == 0)
                    JOptionPane.showMessageDialog(self, "Select search mode");
                if (mode == 1) {
                    String strKey = key.getText();
                    boolean isFound = false;
                    Field field = null;
                    try {

                        field = Telephone.class.getDeclaredField(selectedField);
                        field.setAccessible(true);
                        for (Telephone i : indexMap.values()) {
                            if (field.get(i).toString().equals(strKey)) {
                                isFound=true;
                                JOptionPane.showMessageDialog(self, i.toString());
                            }
                        }
                        if (!isFound) {
                            JOptionPane.showMessageDialog(self, "No such object");
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e1) {
                        e1.printStackTrace();
                    }
                }

                if(mode==2)
                {
                    String strKey = key.getText();
                    boolean isFound = false;
                    Field field = null;
                    try {

                        field = Telephone.class.getDeclaredField(selectedField);
                        field.setAccessible(true);
                        Telephone object=null;
                        for (Telephone i : indexMap.values()) {
                            if (field.get(i).toString().equals(strKey)) {
                                isFound=true;
                                object=i;
                            }
                        }
                        if(isFound){
                            StringBuilder res= new StringBuilder();
                            Object[]keys=indexMap.keySet().toArray();
                            Arrays.sort(keys, Collections.reverseOrder());
                            for (Object i : keys) {
                                if(indexMap.get(i).equals(object)) break;
                                if(!indexMap.get(i).isDeleted()){
                                    res.append(indexMap.get(i).toString()).append("\n");
                                }
                            }
                            if(!res.toString().equals("")) JOptionPane.showMessageDialog(self, res.toString());
                            else JOptionPane.showMessageDialog(self,"this object is the last");
                        }
                        else{
                            JOptionPane.showMessageDialog(self, "No such object");
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e1) {
                        e1.printStackTrace();
                    }

                }

                if(mode==3)
                {
                    String strKey = key.getText();
                    boolean isFound = false;
                    Field field = null;
                    try {

                        field = Telephone.class.getDeclaredField(selectedField);
                        field.setAccessible(true);

                        Telephone object=null;
                        for (Telephone i : indexMap.values()) {
                            if (field.get(i).toString().equals(strKey)) {
                                isFound=true;
                                object=i;
                            }
                        }
                        if(isFound){
                            StringBuilder res= new StringBuilder();
                            for (Object i : indexMap.keySet()) {
                                if(indexMap.get(i).equals(object)) break;
                                if(!indexMap.get(i).isDeleted()){
                                    res.append(indexMap.get(i).toString()).append("\n");
                                }
                            }
                            if(!res.toString().equals("")) JOptionPane.showMessageDialog(self, res.toString());
                            else JOptionPane.showMessageDialog(self,"this object is the first");
                        }
                        else{
                            JOptionPane.showMessageDialog(self, "No such object");
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        load.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser("C:\\Users\\Katty\\IdeaProjects\\Lab_7");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fileChooser.showOpenDialog(self);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File file = fileChooser.getSelectedFile();
                filePath = file.getAbsolutePath();

                try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
                    selectedField="rate";
                    indexMap = Connector.loadMap(randomAccessFile, filePath, selectedField);
                    reversedMap.putAll(indexMap);
                    createListModel(indexMap);
                } catch (IOException | IllegalAccessException | NoSuchFieldException e2) {
                    e2.printStackTrace();
                }
            }
        });


        save.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser("C:\\Users\\Katty\\IdeaProjects\\Lab_7");

                int result = fileChooser.showSaveDialog(self);
                if (result != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();

                try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {

                    selectedField="rate";
                    Connector.saveData(indexMap, randomAccessFile, filePath, selectedField);
                } catch (NoSuchFieldException | IllegalAccessException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            selectedField=(String)comboBox.getSelectedItem();
            indexMap.clear();
            for(Telephone i:reversedMap.values())       {
                indexMap.put(i.getFieldName(selectedField),i);
            }
            reversedMap.clear();
            reversedMap.putAll(indexMap);
            listModel.removeAllElements();
            if(ascendingSorting)
                createListModel(indexMap);
            else
                createListModel(reversedMap);

        } catch (IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
    }
}



