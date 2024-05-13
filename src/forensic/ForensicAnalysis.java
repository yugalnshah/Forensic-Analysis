package forensic;
public class ForensicAnalysis {

    private TreeNode treeRoot; // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis () {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    public void buildTree(String filename) {
        StdIn.setFile(filename); 

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;
        
        int numberOfPeople = Integer.parseInt(StdIn.readLine()); 

        for (int i = 0; i < numberOfPeople; i++) {

            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);

        }
    }

    public Profile createSingleProfile() {

        int s = StdIn.readInt(); // # of STRs associated 
        STR[] arr = new STR[s]; // creates an array of STR objects
        int i = 0;
        while (i < s){ // loops through each STR

            String str = StdIn.readString(); // STR name
            int num = StdIn.readInt(); // # of occurences
            STR obj = new STR(str, num); // new STR object
            arr[i] = obj; // add to the next open space
            i++;

        }
        return new Profile(arr); // create a profile using the data read
    }

    public void insertPerson(String name, Profile newProfile) {

        TreeNode ptr = treeRoot; // root of the BST
        TreeNode prev = null; // keep track of the prev node
        int match = 0; // result of compare

        while (ptr != null){ // traverse until leaf node is reached

            match = name.compareTo(ptr.getName()); // compare given name with the current node
            prev = ptr; // update prev pointer
            if (match > 0) { // go right
                ptr = ptr.getRight();
            } 
            else { // go left
                ptr = ptr.getLeft();
            }

        }

        TreeNode add = new TreeNode(name, newProfile, null, null); // create a new TreeNode

        if (prev == null){
            treeRoot = add;
        }
        else if (match > 0) {
            prev.setRight(add); // set the new node as the right child of prev node
        } 
        else {
            prev.setLeft(add); // set the new node as the left child of prev node
        }
    }

    public int getMatchingProfileCount(boolean isOfInterest) {
        
        return traverseBST(treeRoot, isOfInterest); // start traversing the BST from the root
    }

    // helper method to count matching profiles and traverse the BST
    private int traverseBST(TreeNode node, boolean isOfInterest) {
        int count = 0; // counter for matching profiles
        
        if (node == null){ // base case
            return 0;
        }

        if (node.getProfile() != null && node.getProfile().getMarkedStatus() == isOfInterest) { // check if profile matches
            count = 1; // increment if it matches
        }

        // count profiles in the left and right subtrees recursively
        count += traverseBST(node.getLeft(), isOfInterest);
        count += traverseBST(node.getRight(), isOfInterest);

        return count; // total matching profiles
    }

    private int numberOfOccurrences(String sequence, String STR) {
            
        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;
        
            // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);
        
        while (lastOccurrence != -1) {

            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());

        }
        return repeats;
    }

    public void flagProfilesOfInterest() {

        traverseRec(treeRoot); // start traversing
    }

    // helper method to traverse through the BST
    private void traverseRec(TreeNode node){
        int count = 0, i = 0; // counter for number of matching STRs, and index of the STR array

        if (node == null){ // base case
            return;
        }

        STR[] STRS = node.getProfile().getStrs(); // get the array of STRs of current profile

        while (i < STRS.length){ // loop through each STR

            int numfirst = numberOfOccurrences(getFirstUnknownSequence(), STRS[i].getStrString());
            int numsecond = numberOfOccurrences(getSecondUnknownSequence(), STRS[i].getStrString());
            if ((numfirst + numsecond) == STRS[i].getOccurrences()){
                count++; // increment the counter of matching STR
            }
            i++;

        }
        if (count >= ((STRS.length+1)/2)){ // check if at least half of STRs match
                node.getProfile().setInterestStatus(true); // set the interest status to true
            }
            traverseRec(node.getLeft()); // recursively traverse left
            traverseRec(node.getRight()); // recursively traverse right
    }

    public String[] getUnmarkedPeople() {

        int unmarkedCount = getMatchingProfileCount(false); // number of unmarked profiles
        String[] unmarkedPeople = new String[unmarkedCount]; // new array of Strings for unmarked people
        int index = 0;
        
        Queue<TreeNode> queue = new Queue<>(); // initialize a queue for level order traversal
        if (treeRoot != null)
            queue.enqueue(treeRoot); // enqueue the root if it exists

        while (!queue.isEmpty()) { // perform level-order traversal

            TreeNode node = queue.dequeue();
            if (node != null && node.getProfile() != null && !node.getProfile().getMarkedStatus()) { // check if the node's profile is unmarked
                unmarkedPeople[index++] = node.getName();
            }
            // Enqueue the left and right
            if (node != null && node.getLeft() != null)
                queue.enqueue(node.getLeft());
            if (node != null && node.getRight() != null)
                queue.enqueue(node.getRight());

        }
        return unmarkedPeople; // array of unmarked people
    }

    public void removePerson(String fullName) {

        if (treeRoot.getName().equals(fullName)){ // check if root name matches the given name
            treeRoot = delete(treeRoot, fullName); // update after deletion
        } 
        else {
            delete(treeRoot, fullName); // delete the node with given name
        }

    }

    // helper method to find the minimum node in a subtree
    private TreeNode min (TreeNode node){

        if (node.getLeft() == null){ // traverse left until the minimum node
            return node;
        } 
        else {
            return min(node.getLeft());
        }

    }

    // helper method to delete the minimum node in a subtree
    private TreeNode deleteMin(TreeNode node){

        if (node.getLeft() == null){
            return node.getRight(); // return the right child to replace the deleted node
        }
        node.setLeft(deleteMin(node.getLeft())); // recursively delete the minimum node
        return node;

    }

    // helper method to delete a node with a given name
    private TreeNode delete (TreeNode node, String name){

        if (node == null){ // base case
            return null;
        }

        int compare = name.compareTo(node.getName()); // compare given name with node name

        if (compare < 0){
            node.setLeft(delete(node.getLeft(), name)); // delete in the left subtree if full name is less than nodes name
        } 
        else if (compare > 0){
            node.setRight(delete(node.getRight(), name)); // delete in the right subtree if full name is greater than nodes name
        } 
        else { // delete the node if the name matches
            if (node.getRight() == null){
                return node.getLeft(); // replace with its left child
            }
            if (node.getLeft() == null){
                return node.getRight(); // replace with its right child
            }
            
            // find inorder successor if the node has both left and right children
            TreeNode t = node;
            node = min(t.getRight());
            node.setRight(deleteMin(t.getRight()));
            node.setLeft(t.getLeft());
        }
        return node;

    }

    public void cleanupTree() {

        String[] unmarked = getUnmarkedPeople(); // array of unmarked people
        int i = 0;
        while (i < unmarked.length){ // iterate through each unmarked person

            removePerson(unmarked[i]); // remove the unmarked person
            i++;

        }

    }

    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
