package view;

import businessLogic.Scheduler;
import businessLogic.SelectionPolicy;
import businessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Interfata extends JFrame{
    private JPanel InterfataGrafica;
    private JTextField nrCozi;
    private JComboBox SelectionPolicy;
    private JButton startButton;

    private JTextField nrClienti;
    private JTextField timeLimit;
    private JTextField minArrivalTime;
    private JTextField maxArrivalTime;
    private JTextField minServingTime;
    private JTextField maxServingTime;
    private JTextArea afisareArea;
    private SimulationManager simulation;

    public Interfata(SimulationManager sim)
    {
        simulation=sim;
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userInput1="";
                String userInput2="";
                String userInput3="";
                String userInput4="";
                String userInput5="";
                String userInput6="";
                String userInput7="";
                String userInput8=(String)SelectionPolicy.getSelectedItem();

                try{
                    userInput1=nrCozi.getText();
                    sim.nrCozi=Integer.parseInt(userInput1);

                    userInput2=nrClienti.getText();
                    sim.nrClienti=Integer.parseInt(userInput2);

                    userInput3=timeLimit.getText();
                    sim.timeLimit=Integer.parseInt(userInput3);

                    userInput4=minArrivalTime.getText();
                    sim.minArrivalTime=Integer.parseInt(userInput4);

                    userInput5=maxArrivalTime.getText();
                    sim.maxArrivalTime=Integer.parseInt(userInput5);

                    userInput6=minServingTime.getText();
                    sim.minProcessingTime=Integer.parseInt(userInput6);

                    userInput7=maxServingTime.getText();
                    sim.maxProcessingTime=Integer.parseInt(userInput7);
                    if(userInput8.equals("ShortestTime"))
                        sim.selectionPolicy= businessLogic.SelectionPolicy.SHORTEST_TIME;
                    else
                        sim.selectionPolicy=businessLogic.SelectionPolicy.SHORTEST_QUEUE;

                    sim.setScheduler(new Scheduler(sim.nrCozi));
                    sim.getScheduler().changeStrategy(sim.selectionPolicy);

                    sim.generarteNRandomClients();
                    Thread t=new Thread(sim);
                    t.start();

                }catch(NumberFormatException nfex)
                {
                    showError("Bad input: ");
                    nrCozi.setText("");

                      nrClienti.setText("");
                     timeLimit.setText("");
                     minArrivalTime.setText("");
                     maxArrivalTime.setText("");
                     minServingTime.setText("");
                     maxServingTime.setText("");
                }

            }
        });

        setContentPane(InterfataGrafica);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        afisareArea.setEditable(false);
        setSize(new Dimension(1000,1000));
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public  void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }

    public JTextArea getAfisareArea() {
        return afisareArea;
    }

}
