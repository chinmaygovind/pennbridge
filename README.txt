=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: 68764294
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: NOTE TO GRADER :=
===================
- The file structure is pretty big and complicated. I did my best to show it in file_structure.png,
- but you can download a version of the project here: https://github.com/chinmaygovind/pennbridge
- Alternatively, a Google Drive link is here: https://drive.google.com/drive/folders/1e57reZeWq3ag7y0dF40nphD60Hb8mhY3


===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Inheritance and Subtyping
  - The components of the bridge, including Members, Joints, and previews of Members & Joints are all subtypes of "BridgeComponent".
  - The single "BridgeComponents" class makes it easy to handle all the components of a Bridge at once, as seen in Bridge.java and GamePanel.java.
  - The subclasses of BridgeComponent all have their own draw methods which draw them differently.
  - In addition, the subclasses have other methods which give them different behaviors. (e.g. Joints & Members have different behaviors.)
  - Finally, BridgeComponents is itself a subclass of the abstract "Shape" class, which is the general class I use represent things to draw on the screen.

  2. Collections
  - The project uses collections in many places, but the most interesting use is the Actions list in Bridge.java.
  - The problem with storing the Bridge as a collection of joints and members is that it is impossible to hard to implement an undo feature.
  - As a solution, I store the bridge as a series of "Actions", which can be an AddAction, DeleteAction, or MoveJointAction.
  - These Actions all implement the "Action" interface. Then, Bridge.java stores a LinkedList of actions that the user has performed.
  - When I want to access the joints or members of the bridge, I run a method to "compile" the actions and determine the final configuration of joints and members.

  3. JUnit Testable Component
  - I use JUnit testing to test the Truss model.
  - The Truss model (and entire package) is simply a model of a Truss, composed of members and joints.
  - This truss model is entirely isolated from the GUI, so it makes it easy to test.
  - I use the TrussTest.java class to try applying different forces to various trusses and make sure they match the expected force calculations.

  4. File I/O
  - I have a "Save Bridge" and "Load Bridge" feature that stores the configuration of the bridge to a file.
  - All the data needed to reconstruct the bridge is stored in a ".bridge" file format, modeled after a csv file.

  5?. Advanced Physics
  - I did the statics calculations to calculate the forces applied on the bridge.
  - It approximates the bridge as a "simple truss", and applies Newton's laws and the principles of statics to calculate the forces.
  - Truss.java generates several equations to determine the truss forces, then uses a matrix to represent them.
  - Then, it puts the matrix in reduced row echelon form (rref) to solve for individual forces.
  - The gory details are all in Truss.getForceMatrix() and Truss.solveMemberForces().
  - This is a simplified model that doesn't account for elasticity or deformation, but it works for my purposes.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
    MainFrame: Creates the Main JFrame that display a "Screen" (JPanel).
    MenuPanel, HelpPanel: Simple "Screen"s that extend JPanel that show buttons for Menu & Help Screen.
    GamePanel: a "Screen" that handles most of the GUI logic.
    Bridge: a model of BridgeComponents to store how to draw BridgeComponents to the screen and interface with Truss.
    Truss: a model purely isolated from the GUI to represent the physics calculations for the bridge.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
    Figuring out how to handle an "Undo" feature was difficult.
    It took me a while until I came up with the Actions idea, inspired by the OCaml paint project.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
    There's decent separation between the model and the GUI.
    GamePanel.java handles GUI stuff, and Truss.java handles entirely physics calculations.
    There is a bit of messiness in Bridge.java which interfaces between the two.
    If I had the chance, I might do away with Bridge.java completely and move its methods to GamePanel.java.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

Images:
- Bridge Traveler: http://xkcd.com

Statics Guides:
- https://engineeringstatics.org/Chapter_06.html
- https://en.wikipedia.org/wiki/Euler%27s_critical_load

Others:
- The GUI implementation I used in Bridge.java was based on the PaintE.java shown in lecture.
- I referenced the Oracle Javadocs to help understand some Swing features, like JOptionPane's different message types.
