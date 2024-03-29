module pl.kielce.tu.battleshipsserverv4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jcl.over.slf4j;


    opens pl.kielce.tu.battleshipsserverv4 to javafx.fxml;
    exports pl.kielce.tu.battleshipsserverv4;
}