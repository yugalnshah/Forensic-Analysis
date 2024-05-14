# Forensic Analysis
 
## Overview
I’ve been tasked with identifying who a DNA sequence may belong to based on an unknown person’s DNA sequence and a DNA database I have. A person’s DNA has pairs of chromosomes; I will focus on a single pair of the same chromosome to identify people whose samples we want to investigate further. 

DNA is a molecule found in living organisms that carries our genetic information. It is made up of nucleotides, which are organic building blocks that consist of a base, sugar, and phosphate. Nucleotides contain one of four different bases – adenine (A), thymine (T), cytosine (C), and guanine (G). We will focus on combinations of these four bases in this assignment.

99.9% of our DNA is the same as other humans. Our goal is to focus on the 0.1% of DNA that varies between humans – these are sections of “noncoding DNA” that are in between genes and don’t provide instructions for making proteins. Short Tandem Repeats, or STRs, are in noncoding DNA and are short sets of bases that are repeated across a sequence. 

In this project, I will work with simple DNA sequences with 2-5 STRs. I will identify the number of times a set of STRs is repeated within a sequence to find out who a pair of unknown DNA sequences may belong to in a DNA database.

Why BSTs? In a past semester of CS111, students had a DNA assignment that used an array of objects to store a DNA sequence and find hereditary relationships. When we have a large number of items, we can add/remove a single item more efficiently using a BST in O(log n) time than an array, which takes O(n) time.

## Methods

### 1. createSingleProfile

Given a single person’s data in an input file, read all information and return a new Profile containing the person’s information. 

Input files have been provided to test this method. Each input file is formatted as follows.

* One line containing a string with the person of interest’s FIRST sequence **[DONE in buildTree]**
* One line containing a string the person of interest’s SECOND sequence **[DONE for you in buildTree]**
* One line containing the number of people in the database, say p **[DONE for you in buildTree]**
* For each of **p** people:
     * One line contains first name, last name, and number of STRs (say s), space separated. *Names are read in buildTree!**
     * s lines containing an STR name and number of occurrences in that person’s DNA, space separated
 
To complete this method, do the following:

1. For a single person in the database:
     1. Read the number of STRs (say s) associated with the person. Create an array of STR objects of length s.
     2. For each STR:
          * Read the STR name and number of occurrences.
          * Create a new STR object, and add it to the next open space in the array.
     3. Create a Profile object using the data read, and return this profile.
 
Here is a diagram that illustrates a Profile object.

<img width="634" alt="Screenshot 2024-05-13 at 9 06 27 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/a4cf22ae-c8b7-495d-9616-d185cbb56785">

Here is the expected output when testing **createSingleProfile** using the driver on input1.in. **The driver calls the method on all people but does not update the unknown sequence instance variables. It also skips over names.**

   * **We expect STRs to be in the SAME ORDER as the input files.**

<img width="623" alt="Screenshot 2024-05-13 at 9 08 48 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/b70ba8e8-7a46-4492-90ba-1d9424d8f141">

### 2. insertPerson

Given a full name (key) and Profile object (Value) passed through parameters, add a node containing this name profile to the BST rooted at treeRoot. Remember that treeRoot is a reference to the root node of the BST.

* Follow the BST insertion algorithm as discussed in class to insert Profiles into the BST.
      * Names are unique, nobody will share the same name.
      * Follow BST ordering when inserting a profile. Use the compareTo method on the keys (full names) for comparisons. TreeNodes with names greater than a node go to the right of the 
        node, otherwise they will go to the left.

Here is the expected output when testing this method on input1.in. **Test this method by calling buildTree in the driver, it requires createSingleProfile to be completed.**

<img width="768" alt="Screenshot 2024-05-13 at 9 10 32 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/868c5f68-03a8-4d3b-8f5e-54640dc5c76d">

### 3. flagProfilesOfInterest

This method marks profiles where for all STRs in a profile, most (at least half) of the STR occurrences in a profile match the occurrences of the same STR in the first and second unknown sequences combined. **This is a simplified matching algorithm**, and in reality the identification process is more complex.

* Traverse the BST rooted at treeRoot to investigate whether each profile should be marked.
* For a single profile, it is of interest if at least half of its STRs occur the same amount of times in both the **profile**, and the **first+second** sequence.
* We determine half of STRs by rounding UP to the nearest whole number if there is a remainder. For instance, if there are 3 STRs in a profile, half of 3 is 1.5, which rounds up to 2.

Hint: Use the provided **numberOfOccurrences** method to check the number of times an STR is repeated in a sequence.

Here is the expected output when testing this method on input1.in. **Test buildTree in the driver before testing this method:**

<img width="773" alt="Screenshot 2024-05-13 at 9 12 49 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/860aecbd-764b-442b-a0d5-434c5aa2343f">

### 4. getMatchingProfileCount

Find the number of profiles in the BST rooted at treeRoot whose marked status matches isOfInterest. Return this number. If there are no profiles that are marked according to the parameter **isOfInterest**, return 0.

* Recall that Profiles are values stored inside nodes. The **Profile.java** class contains a boolean instance variable **isMarked** and getters and setters. By default, all profiles are marked 
  false (not profiles of interest).
* **isMarked is a boolean value: true represents “marked” and false represents unmarked.**
* Implement this method recursively. You are encouraged to use helper methods, just make them private.

Here is the expected output when testing this method on input1.in. **Test buildTree and flagProfilesOfInterest in that order in the driver before testing this method.**

<img width="908" alt="Screenshot 2024-05-13 at 9 15 29 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/3e6b1153-08a1-4afa-9c35-5b55c8bd7d36">

### 5. getUnmarkedPeople

This method uses a level-order traversal to populate and return an array of unmarked profiles in the BST rooted at treeRoot.

* Use the **getMatchingProfileCount** method you implemented earlier to get the array length. Create a new array of Strings using the number of unmarked profiles as its length.
* Use the provided Queue class to initialize a queue of TreeNodes. Until the queue is empty:
     * For each node, investigate whether its profile is marked or not. 
          * If its profile is not marked, add **the PERSON’S NAME (key)** to the next available space in your resulting array.
          * Enqueue the node’s children.
* To initialize a queue of TreeNodes, do: Queue<TreeNode> queue = new Queue<>();
     * **queue.enqueue(item)** enqueues the parameter item into a queue.
     * **queue.dequeue()** dequeues an item as discussed in class.
     * **queue.isEmpty()** checks if the queue is empty.
* Return the resulting array.
 
**Algorithm Example:**

* The queue starts as empty. Start by enqueuing the root (at position 1).
* Dequeue the node at position 1, visit it, and enqueue its left and right children (positions 2 and 3).
* Repeat the same procedure on all other nodes until the queue is empty:
     * Dequeue the node at position 2, visit it, and enqueue its children.
     * Dequeue the node at position 3, visit it, and enqueue its children.
     * Lastly, dequeue and visit positions 4 and 5. They are leaves.
 <img width="326" alt="Screenshot 2024-05-13 at 9 18 16 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/683fa446-f58a-4a19-a029-f3de735e56d7">

Here is the output for this method when testing this method on input1.in. **Test buildTree and flagProfilesOfInterest in that order in the driver before testing this method, and IMPLEMENT getMatchingProfileCount before starting this method.**

<img width="766" alt="Screenshot 2024-05-13 at 9 19 44 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/b3a1160f-ab52-48f0-a5a9-9ffade67dd0d">

### 6. removePerson

This method removes a node whose name matches parameters. This is similar to the BST delete you have seen in class.

* Compare full names (“Last, First” including the comma and space) by using the compareTo method on keys as shown in class.
* If there are no left or right children, delete the node by setting the parent to null.
* If there is only one child, replace the parent link.
* If there are two children, find the inorder successor of the node, delete the minimum in its right subtree, and put the inorder successor in the node’s spot.
* Note: if the root of the BST changes treeRoot should be updated accordingly.

Here is the output for this method when testing this method on input1.in using “Doe, John”. **Test buildTree in the driver before testing this method, and make sure all methods above are implemented before moving onto the next method. If you have tested other methods, start over.**

<img width="872" alt="Screenshot 2024-05-13 at 9 20 54 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/33cfc55e-e8c9-42e5-a34d-e48660843e8a">

### 7. cleanupTree

This method removes nodes containing unmarked profiles from the BST.

* Use the getUnmarkedPeople method to get an array of unmarked people.
* Use the removePerson method on each unmarked person, passing as parameters the full name of the person.

Here is the output for this method when testing this method on input1.in. **Test buildTree and flagProfilesOfInterest in the driver in this order before testing this method, and make sure ALL methods are implemented. Start over if you have called removeProfile in the driver immediately before calling cleanupTree.**

<img width="793" alt="Screenshot 2024-05-13 at 9 21 38 PM" src="https://github.com/yugalnshah/Forensic-Analysis/assets/162384655/24f8f877-2acd-4956-a90c-92701d0bf742">
