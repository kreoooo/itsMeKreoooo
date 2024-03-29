module pl.kielce.tu.battleshipsclientv4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.kielce.tu.battleshipsclientv4 to javafx.fxml;
    exports pl.kielce.tu.battleshipsclientv4;
}