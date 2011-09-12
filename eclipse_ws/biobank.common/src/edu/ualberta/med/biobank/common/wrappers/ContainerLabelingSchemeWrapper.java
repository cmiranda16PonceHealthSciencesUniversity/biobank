package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerLabelingSchemeBaseWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerLabelingSchemeWrapper extends
    ContainerLabelingSchemeBaseWrapper {

    public static final int SCHEME_SBS = 1;

    public static final int SCHEME_CBSR_2_CHAR_ALPHA = 2;

    public static final int SCHEME_2_CHAR_NUMERIC = 3;

    public static final int SCHEME_DEWAR = 4;

    public static final int SCHEME_CBSR_SBS = 5;

    public static final int SCHEME_2_CHAR_ALPHA = 6;

    public static final String CBSR_2_CHAR_LABELLING_PATTERN = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static final String TWO_CHAR_LABELLING_PATTERN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHIJKLMNOP";

    public static String BOX81_LABELLING_PATTERN = "ABCDEFGHJ";

    private static Map<Integer, ContainerLabelingSchemeWrapper> allSchemes = null;

    public ContainerLabelingSchemeWrapper(
        WritableApplicationService appService,
        ContainerLabelingScheme wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerLabelingSchemeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public static synchronized Map<Integer, ContainerLabelingSchemeWrapper> getAllLabelingSchemesMap(
        WritableApplicationService appService) throws ApplicationException {
        if (allSchemes == null) {
            allSchemes = new HashMap<Integer, ContainerLabelingSchemeWrapper>();
            List<ContainerLabelingScheme> list = appService.search(
                ContainerLabelingScheme.class, new ContainerLabelingScheme());
            if (list != null) {
                for (ContainerLabelingScheme scheme : list) {
                    Integer id = scheme.getId();
                    switch (id.intValue()) {
                    case SCHEME_SBS:
                        if (!scheme.getName().equals("SBS Standard")) {
                            throw new ApplicationException(
                                "labeling scheme is not " + scheme.getName());
                        }
                        break;

                    case SCHEME_CBSR_2_CHAR_ALPHA:
                        if (!scheme.getName().equals("CBSR 2 char alphabetic")) {
                            throw new ApplicationException(
                                "labeling scheme is not " + scheme.getName());
                        }
                        break;

                    case SCHEME_2_CHAR_NUMERIC:
                        if (!scheme.getName().equals("2 char numeric")) {
                            throw new ApplicationException(
                                "labeling scheme is not " + scheme.getName());
                        }
                        break;

                    case SCHEME_DEWAR:
                        if (!scheme.getName().equals("Dewar")) {
                            throw new ApplicationException(
                                "labeling scheme is not " + scheme.getName());
                        }
                        break;

                    case SCHEME_CBSR_SBS:
                        if (!scheme.getName().equals("CBSR SBS")) {
                            throw new ApplicationException(
                                "labeling scheme is not " + scheme.getName());
                        }
                        break;

                    case SCHEME_2_CHAR_ALPHA:
                        if (!scheme.getName().equals("2 char alphabetic")) {
                            throw new ApplicationException(
                                "labeling scheme is not " + scheme.getName());
                        }
                        break;

                    default:
                        throw new ApplicationException(
                            "labeling scheme with id " + id
                                + " is not mapped correctly");
                    }
                    allSchemes.put(id, new ContainerLabelingSchemeWrapper(
                        appService, scheme));
                }
            }
        }
        return allSchemes;
    }

    public static ContainerLabelingSchemeWrapper getLabelingSchemeById(
        WritableApplicationService appService, Integer id)
        throws ApplicationException {
        getAllLabelingSchemesMap(appService);
        ContainerLabelingSchemeWrapper scheme = allSchemes.get(id);
        if (scheme == null) {
            throw new ApplicationException("labeling scheme with id " + id
                + " does not exist");
        }
        return scheme;
    }

    @Override
    public int compareTo(ModelWrapper<ContainerLabelingScheme> o) {
        return 0;
    }

    @Override
    public void persist() throws Exception {
        super.persist();
        resetAllSchemes();
    }

    @Override
    public void delete() throws Exception {
        super.delete();
        resetAllSchemes();
    }

    private static synchronized void resetAllSchemes() {
        allSchemes = null;
    }

    /**
     * Check labeling scheme limits for a given gridsize
     **/
    public static boolean checkBounds(WritableApplicationService appService,
        Integer labelingScheme, Integer totalRows, Integer totalCols) {

        if (totalRows == null || totalRows <= 0 || totalCols == null
            || totalCols <= 0) {
            return false;
        }

        try {
            getAllLabelingSchemesMap(appService);
        } catch (ApplicationException e) {
            throw new RuntimeException(
                "could not load container labeling schemes");
        }

        ContainerLabelingSchemeWrapper schemeWrapper = allSchemes
            .get(labelingScheme);
        if (schemeWrapper != null) {
            return schemeWrapper.checkBounds(totalRows, totalCols);
        }
        return false;
    }

    /**
     * Check labeling scheme limits for a given gridsize
     **/
    public boolean checkBounds(int totalRows, int totalCols) {
        Integer maxRows = getMaxRows();
        Integer maxCols = getMaxCols();
        Integer maxCapacity = getMaxCapacity();

        boolean isInBounds = true;

        if (maxRows != null) {
            isInBounds &= totalRows <= maxRows;
        }

        if (maxCols != null) {
            isInBounds &= totalCols <= maxCols;
        }

        if (maxCapacity != null) {
            isInBounds &= totalRows * totalCols <= maxCapacity;
        }

        return isInBounds;
    }

    public static boolean canLabel(ContainerLabelingScheme scheme,
        Capacity capacity) {
        boolean canLabel = true;

        if (canLabel && scheme.getMaxRows() != null) {
            canLabel &= capacity.getRowCapacity() <= scheme.getMaxRows();
        }

        if (canLabel && scheme.getMaxCols() != null) {
            canLabel &= capacity.getColCapacity() <= scheme.getMaxCols();
        }

        if (canLabel && scheme.getMaxCapacity() != null) {
            int max = capacity.getRowCapacity() * capacity.getColCapacity();
            canLabel &= max <= scheme.getMaxCapacity();
        }

        return canLabel;
    }

    private static final String POS_LABEL_LEN_QRY = "select min(minChars), max(maxChars) from "
        + ContainerLabelingScheme.class.getName();

    public static List<Integer> getPossibleLabelLength(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria rangeQuery = new HQLCriteria(POS_LABEL_LEN_QRY);
        Object[] minMax = (Object[]) appService.query(rangeQuery).get(0);
        List<Integer> validLengths = new ArrayList<Integer>();
        for (int i = (Integer) minMax[0]; i < (Integer) minMax[1] + 1; i++) {
            validLengths.add(i);
        }
        return validLengths;
    }

    /**
     * Get the rowColPos corresponding to the given SBS standard 2 or 3 char
     * string position. Could be A2 or F12.
     */
    public static RowColPos sbsToRowCol(WritableApplicationService appService,
        String pos) throws Exception {
        ContainerLabelingSchemeWrapper scheme = getLabelingSchemeById(
            appService, SCHEME_SBS);
        if (scheme == null) {
            throw new BiobankCheckException(
                "SBS Standard labeling scheme not found");
        }
        if ((pos.length() != scheme.getMinChars())
            && (pos.length() != scheme.getMaxChars())) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        int row = SBS_ROW_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

    /**
     * Get the rowColPos corresponding to the given CBSR SBS 2 or 3 char string
     * position. Could be A2 or F12. (CBSR SBS skip I and O)
     */
    public static RowColPos cbsrSbsToRowCol(
        WritableApplicationService appService, String pos) throws Exception {
        ContainerLabelingSchemeWrapper scheme = getLabelingSchemeById(
            appService, SCHEME_CBSR_SBS);
        if (scheme == null) {
            throw new BiobankCheckException(
                "CBSR SBS labeling scheme not found");
        }
        if ((pos.length() != scheme.getMinChars())
            && (pos.length() != scheme.getMaxChars())) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        int row = BOX81_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

    /**
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard. 2:1 will return C2.
     */
    public static String rowColToSbs(RowColPos rcp) {
        return "" + SBS_ROW_LABELLING_PATTERN.charAt(rcp.getRow())
            + (rcp.getCol() + 1);
    }

    /**
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard. 2:1 will return C2.
     */
    private static String rowColtoCbsrSbs(RowColPos rcp) {
        return "" + BOX81_LABELLING_PATTERN.charAt(rcp.getRow())
            + (rcp.getCol() + 1);
    }

    /**
     * get the RowColPos in the given container corresponding to the given label
     * AB and will return 1:0.
     */
    public static RowColPos cbsrTwoCharToRowCol(
        WritableApplicationService appService, String label, int rowCap,
        int colCap, String containerTypeName) throws Exception {
        ContainerLabelingSchemeWrapper scheme = getLabelingSchemeById(
            appService, SCHEME_CBSR_2_CHAR_ALPHA);
        if (scheme == null) {
            throw new BiobankCheckException(
                "CBSR 2 char alphabetic labeling scheme not found");
        }
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception("Label should be " + scheme.getMinChars()
                + " characters");

        int index1 = CBSR_2_CHAR_LABELLING_PATTERN.indexOf(label
            .charAt(len - 2));
        int index2 = CBSR_2_CHAR_LABELLING_PATTERN.indexOf(label
            .charAt(len - 1));
        if ((index1 < 0) || (index2 < 0)) {
            throw new Exception(
                "Invalid characters in label. Are they in upper case?");
        }
        int pos = index1 * CBSR_2_CHAR_LABELLING_PATTERN.length() + index2;

        if (pos >= rowCap * colCap) {
            String maxValue = ContainerLabelingSchemeWrapper
                .rowColToCbsrTwoChar(new RowColPos(rowCap - 1, colCap - 1),
                    rowCap, colCap);
            throw new Exception("Address  " + label + " does not exist"
                + (containerTypeName == null ? "" : " in " + containerTypeName)
                + ". Max value is " + maxValue + ". (Max row: " + rowCap
                + ". Max col: " + colCap + ".)");
        }
        Integer row = pos % rowCap;
        Integer col = pos / rowCap;
        RowColPos rowColPos = new RowColPos(row, col);
        return rowColPos;

    }

    /**
     * get the RowColPos in the given container corresponding to the given label
     * AB and will return 1:0.
     */
    public static RowColPos twoCharToRowCol(
        WritableApplicationService appService, String label, int rowCap,
        int colCap, String containerTypeName) throws Exception {
        ContainerLabelingSchemeWrapper scheme = getLabelingSchemeById(
            appService, SCHEME_2_CHAR_ALPHA);
        if (scheme == null) {
            throw new BiobankCheckException(
                "2 char alphabetic labeling scheme not found");
        }
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception("Label should be " + scheme.getMinChars()
                + " characters");

        int index1 = TWO_CHAR_LABELLING_PATTERN.indexOf(label.charAt(len - 2));
        int index2 = TWO_CHAR_LABELLING_PATTERN.indexOf(label.charAt(len - 1));
        if ((index1 < 0) || (index2 < 0)) {
            throw new Exception(
                "Invalid characters in label. Are they in upper case?");
        }
        int pos = index1 * TWO_CHAR_LABELLING_PATTERN.length() + index2;

        if (pos >= rowCap * colCap) {
            String maxValue = ContainerLabelingSchemeWrapper
                .rowColToCbsrTwoChar(new RowColPos(rowCap - 1, colCap - 1),
                    rowCap, colCap);
            throw new Exception("Address  " + label + " does not exist"
                + (containerTypeName == null ? "" : " in " + containerTypeName)
                + ". Max value is " + maxValue + ". (Max row: " + rowCap
                + ". Max col: " + colCap + ".)");
        }
        Integer row = pos % rowCap;
        Integer col = pos / rowCap;
        RowColPos rowColPos = new RowColPos(row, col);
        return rowColPos;

    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the 2 char numeric labelling.
     */
    public static RowColPos twoCharNumericToRowCol(
        WritableApplicationService appService, String label, int totalRows)
        throws Exception {
        ContainerLabelingSchemeWrapper scheme = getLabelingSchemeById(
            appService, SCHEME_2_CHAR_NUMERIC);
        if (scheme == null) {
            throw new BiobankCheckException(
                "2 char numeric labeling scheme not found");
        }
        String errorMsg = "Label " + label
            + " is incorrect: it should be 2 characters";
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception(errorMsg);
        try {
            int pos = Integer.parseInt(label) - 1;
            // has remove 1 because the two char numeric starts at 1
            Integer row = pos % totalRows;
            Integer col = pos / totalRows;
            RowColPos rowColPos = new RowColPos(row, col);
            return rowColPos;
        } catch (NumberFormatException nbe) {
            throw new Exception(errorMsg);
        }
    }

    /**
     * Convert a position in row*column to two letter (in the CBSR way)
     * 
     * @throws Exception
     * 
     * @throws BiobankCheckException
     */
    public static String rowColToCbsrTwoChar(RowColPos rcp, int totalRows,
        int totalCols) {
        int pos1, pos2, index;
        int lettersLength = CBSR_2_CHAR_LABELLING_PATTERN.length();
        if (totalRows == 1) {
            index = rcp.getCol();
        } else if (totalCols == 1) {
            index = rcp.getRow();
        } else {
            index = totalRows * rcp.getCol() + rcp.getRow();
        }

        pos1 = index / lettersLength;
        pos2 = index % lettersLength;

        if (pos1 >= 0 && pos2 >= 0) {
            return String.valueOf(CBSR_2_CHAR_LABELLING_PATTERN.charAt(pos1))
                + String.valueOf(CBSR_2_CHAR_LABELLING_PATTERN.charAt(pos2));
        }
        return null;
    }

    /**
     * Convert a position in row*column to two letter (in the CBSR way)
     * 
     * @throws Exception
     * 
     * @throws BiobankCheckException
     */
    public static String rowColToTwoChar(RowColPos rcp, int totalRows,
        int totalCols) {
        int pos1, pos2, index;
        int lettersLength = TWO_CHAR_LABELLING_PATTERN.length();
        if (totalRows == 1) {
            index = rcp.getCol();
        } else if (totalCols == 1) {
            index = rcp.getRow();
        } else {
            index = totalRows * rcp.getCol() + rcp.getRow();
        }

        pos1 = index / lettersLength;
        pos2 = index % lettersLength;

        if (pos1 >= 0 && pos2 >= 0) {
            return String.valueOf(TWO_CHAR_LABELLING_PATTERN.charAt(pos1))
                + String.valueOf(TWO_CHAR_LABELLING_PATTERN.charAt(pos2));
        }
        return null;
    }

    /**
     * Convert a position in row*column to two char numeric.
     */
    public static String rowColToTwoCharNumeric(RowColPos rcp, int totalRows) {
        return String.format("%02d", rcp.getRow() + totalRows * rcp.getCol()
            + 1);
    }

    /**
     * Convert a position in row*column to Dewar labelling (AA, BB, CC...).
     */
    public static String rowColToDewar(RowColPos rcp, Integer colCapacity) {
        int pos = rcp.getCol() + (colCapacity * rcp.getRow());
        String letter = String.valueOf(CBSR_2_CHAR_LABELLING_PATTERN
            .charAt(pos));
        return letter + letter;
    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the dewar labelling.
     * 
     * @throws Exception
     */
    public static RowColPos dewarToRowCol(
        WritableApplicationService appService, String label, int totalCol)
        throws Exception {
        ContainerLabelingSchemeWrapper scheme = getLabelingSchemeById(
            appService, SCHEME_DEWAR);
        if (scheme == null) {
            throw new BiobankCheckException(
                "CBSR 2 char alphabetic labeling scheme not found");
        }
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception("Label should be " + scheme.getMinChars()
                + " characters");

        if (label.charAt(0) != label.charAt(1)) {
            throw new Exception("Label should be double letter (BB).");
        }
        // letters are double (BB). need only one
        int letterPosition = SBS_ROW_LABELLING_PATTERN.indexOf(label.charAt(0));
        Integer row = letterPosition / totalCol;
        Integer col = letterPosition % totalCol;
        RowColPos rowColPos = new RowColPos(row, col);
        return rowColPos;
    }

    /**
     * Get the 2 char string corresponding to a RowColPos position given the
     * container capacity
     */
    public static String getPositionString(RowColPos rcp,
        Integer childLabelingSchemeId, Integer rowCapacity, Integer colCapacity) {
        switch (childLabelingSchemeId) {
        case 1:
            // SBS standard
            return rowColToSbs(rcp);
        case 2:
            // CBSR 2 char alphabetic
            return rowColToCbsrTwoChar(rcp, rowCapacity, colCapacity);
        case 3:
            // 2 char numeric
            return rowColToTwoCharNumeric(rcp, rowCapacity);
        case 4:
            // dewar
            return rowColToDewar(rcp, colCapacity);
        case 5:
            // CBSR SBS
            return rowColtoCbsrSbs(rcp);
        case 6:
            // 2 char alphabetic
            return rowColToTwoChar(rcp, rowCapacity, colCapacity);
        }
        return null;
    }

    /**
     * Get the RowColPos position corresponding to the string position given the
     * container capacity
     */
    public static RowColPos getRowColFromPositionString(
        WritableApplicationService appService, String position,
        Integer childLabelingSchemeId, Integer rowCapacity, Integer colCapacity)
        throws Exception {
        switch (childLabelingSchemeId) {
        case 1:
            // SBS standard
            return sbsToRowCol(appService, position);
        case 2:
            // CBSR 2 char alphabetic
            return cbsrTwoCharToRowCol(appService, position, rowCapacity,
                colCapacity, null);
        case 3:
            // 2 char numeric
            return twoCharNumericToRowCol(appService, position, rowCapacity);
        case 4:
            // Dewar
            return dewarToRowCol(appService, position, colCapacity);
        case 5:
            // Box81
            return cbsrSbsToRowCol(appService, position);
        }
        return null;
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {

        tasks.add(check().notUsedBy(ContainerType.class,
            ContainerTypePeer.CHILD_LABELING_SCHEME));

        super.addDeleteTasks(tasks);
    }
}
