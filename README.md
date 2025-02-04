# Kruskal-mst

How to use the software:

1- Navigate to the main folder **Kruskal-mst**
2- Once inside this folder, compile using the command:
   ```sh
   javac --module-path [lib/javafx-sdk-21/lib] --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")
   ```
3- Run with:
   ```sh
   java --module-path [lib/javafx-sdk-21/lib] --add-modules javafx.controls,javafx.fxml -cp out App
   ```

**Note:** The path inside the square brackets must be replaced with the actual path where the `lib` folder of the **javafx-sdk** is located. This folder is included in the project, e.g.:
   ```
   /University/Algorithms_and_Data_Structures/Project//Kruskal-mst/javafx-sdk-21.0.5/lib
   ```

---

### Once the software is executed, the GUI will display:

1. A **spinner** for selecting the number of nodes, up to a maximum of **5000**, depending on the graph on which the algorithm will be executed, and a **button** to confirm the node selection.

2. A **"Load CSV" button**, which requires a CSV file containing the graph information. We assume the CSV structure to be:
   ```
   (Starting Node, Destination Node, Edge Weight)
   ```
   Clicking the button will open a dialog window to select the test folder and then choose a CSV file for generating the graph.

3. The corresponding graph for the selected CSV file will be generated. You can click on a node to view its details. To execute the algorithm, press the **"Run Algorithm"** button. This will update:
   - The **progress bar** indicating the execution progress
   - The **execution time** of the algorithm
   - A **textual representation** of the Minimum Spanning Tree (MST)

4. Finally, clicking the **reset button** will clear the UI, allowing you to test a new graph.

---

### [COMPILATION AND RUN COMMANDS FOR LINUX]
```sh
javac --module-path /home/alessandro/University/Algorithms_and_Data_Structures/Project/Kruskal-mst/javafx-sdk-21.0.5/lib --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")
```
```sh
java --module-path /home/alessandro/University/Algorithms_and_Data_Structures/Project/Kruskal-mst/javafx-sdk-21.0.5/lib --add-modules javafx.controls,javafx.fxml -cp out App
```

