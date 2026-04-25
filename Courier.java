import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;

public class Courier extends JFrame {

    // ================= ADMIN LOGIN =================
    static final String ADMIN_ID = "admin", ADMIN_PASS = "admin";

    // ================= DATA STORE =================
    HashMap<String, CourierData> db = new HashMap<>();

    // ================= FONTS =================
    static final Font FB = new Font("Arial", Font.BOLD, 13),
                      FP = new Font("Arial", Font.PLAIN, 13);

    // ================= COLORS =================
    static final Color BLUE = new Color(100,150,210),
        GREEN = new Color(100,180,130),
        TEAL = new Color(130,160,220),
        ROSE = new Color(180,130,130),
        TITLE = new Color(70,130,180),
        LBL = new Color(80,110,160),
        OUT = new Color(60,100,140),
        ERR = new Color(200,80,80),
        OK = new Color(60,140,100);

    public Courier() {
        setTitle("Courier Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        showLogin();
        setVisible(true);
    }

    // ============================================================
    // 🔑 LOGIN SCREEN
    // ============================================================
    void showLogin() {
        reset(420,320);

        JLabel t = lbl("Courier Management System", TITLE,
                new Font("Arial",Font.BOLD,16));
        t.setBorder(new EmptyBorder(20,10,10,10));
        add(t, BorderLayout.NORTH);

        JPanel c = new JPanel(new GridBagLayout());

        c.add(lbl("Please select your role to continue:",
                new Color(110,130,160), FP), gbc(0,0,2));

        JButton a = btn("Login as Admin", BLUE);
        JButton u = btn("Login as User", TEAL);

        c.add(a, gbc(0,1,1));
        c.add(u, gbc(1,1,1));

        add(c, BorderLayout.CENTER);

        add(lbl("Admin: Full Access  |  User: Track Only",
                new Color(160,160,180),
                new Font("Arial",Font.ITALIC,11)), BorderLayout.SOUTH);

        refresh();

        a.addActionListener(e -> adminDialog());
        u.addActionListener(e -> showUser());
    }

    // ============================================================
    // 🔐 ADMIN LOGIN
    // ============================================================
    void adminDialog() {

        JDialog d = new JDialog(this,"Admin Login",true);
        d.setSize(320,210);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());

        JTextField idF = field(14);
        JPasswordField pF = new JPasswordField(14);
        pF.setFont(FP);

        JLabel err = lbl("", ERR, new Font("Arial",Font.ITALIC,11));
        JButton login = btn("Login", BLUE);

        d.add(lbl("Admin ID:",LBL,FB), gbc(0,0,1));
        d.add(idF, gbc(1,0,1));

        d.add(lbl("Password:",LBL,FB), gbc(0,1,1));
        d.add(pF, gbc(1,1,1));

        d.add(err, gbc(0,2,2));
        d.add(login, gbc(0,3,2));

        Runnable check = () -> {
            if(idF.getText().trim().equals(ADMIN_ID)
            && new String(pF.getPassword()).equals(ADMIN_PASS)) {
                d.dispose();
                showAdmin();
            } else {
                err.setText("Invalid Admin ID or Password!");
                pF.setText("");
            }
        };

        login.addActionListener(e -> check.run());
        pF.addActionListener(e -> check.run());

        d.setVisible(true);
    }

    // ============================================================
    // 🧑‍💼 ADMIN PANEL
    // ============================================================
    void showAdmin() {

        reset(520,520);

        JPanel head = new JPanel(new BorderLayout());
        head.setBorder(new EmptyBorder(10,12,6,12));

        head.add(lbl("Admin Panel  —  Full Access", TITLE,
                new Font("Arial",Font.BOLD,16)), BorderLayout.WEST);

        JButton lo = smallBtn("Logout", ROSE);
        head.add(lo, BorderLayout.EAST);

        // INPUT FIELDS
        JTextField id = field(0);
        JTextField sender = field(0);
        JTextField receiver = field(0);

        JComboBox<String> status = new JComboBox<>(new String[]{
                "Courier Added","In Transit","Out for Delivery","Delivered"
        });

        JPanel form = new JPanel(new GridLayout(4,2,6,6));
        form.setBorder(titled("Courier Details"));

        String[] L = {"Tracking ID:","Sender Name:","Receiver Name:","Status:"};
        Component[] F = {id,sender,receiver,status};

        for(int i=0;i<4;i++){
            form.add(lbl(L[i],LBL,FB));
            form.add(F[i]);
        }

        // OUTPUT
        JTextArea out = output();
        JScrollPane sc = scroll(out,"Output");

        JButton add = btn("Add Courier",GREEN);
        JButton upd = btn("Update Status",BLUE);

        JPanel bp = new JPanel();
        bp.add(add);
        bp.add(upd);

        add(head,BorderLayout.NORTH);
        add(stack(form,bp,sc),BorderLayout.CENTER);
        refresh();

        // ADD COURIER
        add.addActionListener(e -> {

            String i = id.getText().trim();

            if(i.isEmpty()){
                show(out,"Tracking ID empty!",ERR);
                return;
            }

            if(db.containsKey(i)){
                show(out,"ID already exists!",ERR);
                return;
            }

            CourierData c = new CourierData(
                    i,
                    sender.getText().trim(),
                    receiver.getText().trim(),
                    status.getSelectedItem().toString()
            );

            db.put(i,c);

            show(out,
                    "Courier Added!\n\nID: "+c.id+
                    "\nSender: "+c.sender+
                    "\nReceiver: "+c.receiver+
                    "\nStatus: "+c.status,OK);
        });

        // UPDATE STATUS
        upd.addActionListener(e -> {

            CourierData c = db.get(id.getText().trim());

            if(c==null){
                show(out,"Not found!",ERR);
                return;
            }

            c.status = status.getSelectedItem().toString();

            show(out,
                    "Updated!\n\nID: "+c.id+
                    "\nStatus: "+c.status,OK);
        });

        lo.addActionListener(e -> showLogin());
    }

    // ============================================================
    // 👤 USER PANEL
    // ============================================================
    void showUser() {

        reset(440,360);

        JPanel head = new JPanel(new BorderLayout());
        head.setBorder(new EmptyBorder(10,12,6,12));

        head.add(lbl("User Panel  —  Track Only", TITLE,
                new Font("Arial",Font.BOLD,16)), BorderLayout.WEST);

        JButton lo = smallBtn("Logout", ROSE);
        head.add(lo, BorderLayout.EAST);

        JTextField id = field(0);

        JPanel p = new JPanel(new GridLayout(1,2));
        p.setBorder(titled("Track Courier"));

        p.add(lbl("Tracking ID:",LBL,FB));
        p.add(id);

        JTextArea out = output();
        JScrollPane sc = scroll(out,"Output");

        JButton tr = btn("Track Courier",TEAL);

        add(head,BorderLayout.NORTH);
        add(stack(p,tr,sc),BorderLayout.CENTER);
        refresh();

        // TRACK
        tr.addActionListener(e -> {

            CourierData c = db.get(id.getText().trim());

            if(c==null){
                show(out,"Not found!",ERR);
                return;
            }

            show(out,
                    "ID: "+c.id+
                    "\nSender: "+c.sender+
                    "\nReceiver: "+c.receiver+
                    "\nStatus: "+c.status,OUT);
        });

        lo.addActionListener(e -> showLogin());
    }

    // ============================================================
    // 🧩 HELPERS (UI SHORT METHODS)
    // ============================================================
    void reset(int w,int h){
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        setSize(w,h);
        setLocationRelativeTo(null);
    }

    void refresh(){ revalidate(); repaint(); }

    void show(JTextArea a,String m,Color c){
        a.setForeground(c);
        a.setText(m);
    }

    JLabel lbl(String t,Color c,Font f){
        JLabel l=new JLabel(t,SwingConstants.CENTER);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    JTextField field(int c){
        JTextField f=c>0?new JTextField(c):new JTextField();
        f.setFont(FP);
        return f;
    }

    JTextArea output(){
        JTextArea a=new JTextArea(7,35);
        a.setEditable(false);
        a.setFont(new Font("Courier New",Font.PLAIN,13));
        return a;
    }

    JScrollPane scroll(JTextArea a,String t){
        return new JScrollPane(a);
    }

    Border titled(String t){
        return new TitledBorder(t);
    }

    JPanel stack(Component...c){
        JPanel p=new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        for(Component x:c) p.add(x);
        return p;
    }

    GridBagConstraints gbc(int x,int y,int w){
        GridBagConstraints g=new GridBagConstraints();
        g.gridx=x; g.gridy=y; g.gridwidth=w;
        g.insets=new Insets(6,10,6,10);
        g.fill=GridBagConstraints.HORIZONTAL;
        return g;
    }

    JButton btn(String t,Color b){
        JButton x=new JButton(t);
        x.setBackground(b);
        x.setForeground(Color.WHITE);
        x.setFocusPainted(false);
        return x;
    }

    JButton smallBtn(String t,Color b){
        JButton x=btn(t,b);
        x.setFont(new Font("Arial",Font.PLAIN,11));
        return x;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(Courier::new);
    }
}

// ============================================================
// 📦 MODEL CLASS
// ============================================================
class CourierData {
    String id, sender, receiver, status;

    CourierData(String i,String s,String r,String st){
        id=i; sender=s; receiver=r; status=st;
    }
}