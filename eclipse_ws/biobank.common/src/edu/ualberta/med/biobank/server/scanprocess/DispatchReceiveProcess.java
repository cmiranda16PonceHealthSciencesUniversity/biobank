package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.data.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DispatchReceiveProcess extends ServerProcess {

    private int pendingSpecimenNumber = 0;

    List<Integer> extraSpecimens = null;

    public DispatchReceiveProcess(WritableApplicationService appService,
        DispatchProcessData data, User user) {
        super(appService, data, user);
    }

    /**
     * Process of a map of cells
     */
    @Override
    protected ScanProcessResult getScanProcessResult(
        Map<RowColPos, Cell> cells, boolean isRescanMode) throws Exception {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells, receiveProcess(cells));
        return res;
    }

    /**
     * Process of only one cell
     */
    @Override
    protected CellProcessResult getCellProcessResult(Cell cell)
        throws Exception {
        CellProcessResult res = new CellProcessResult();
        processCellDipatchReceiveStatus(cell);
        res.setResult(cell);
        return res;
    }

    /**
     * Process cells in receive mode
     * 
     * @param cells
     * @return
     * @throws Exception
     */
    private CellStatus receiveProcess(Map<RowColPos, Cell> cells)
        throws Exception {
        pendingSpecimenNumber = 0;
        CellStatus currentScanState = CellStatus.EMPTY;
        if (cells != null) {
            extraSpecimens = new ArrayList<Integer>();
            for (Cell cell : cells.values()) {
                processCellDipatchReceiveStatus(cell);
                currentScanState = currentScanState.mergeWith(cell.getStatus());
            }
        }
        return currentScanState;
    }

    /**
     * Update cell data with this specimen
     * 
     * @param cell
     * @param specimen
     */
    private void updateCellWithSpecimen(Cell cell, SpecimenWrapper specimen) {
        cell.setSpecimenId(specimen.getId());
        cell.setTitle(specimen.getCollectionEvent().getPatient().getPnumber());
    }

    /**
     * Processing a cell in receive mode
     * 
     * @param cell
     * @throws Exception
     */
    private void processCellDipatchReceiveStatus(Cell cell) throws Exception {
        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(appService,
            cell.getValue(), user);
        if (foundSpecimen == null) {
            // not in db
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(Messages.getString(
                "DispatchReceiveScanDialog.cell.notInDb.msg", cell.getValue())); //$NON-NLS-1$
            cell.setTitle("!"); //$NON-NLS-1$
        } else {
            DispatchSpecimenState state = ((DispatchProcessData) data)
                .getCurrentDispatchSpecimenIds().get(foundSpecimen.getId());
            if (state == null) {
                // not in the shipment
                updateCellWithSpecimen(cell, foundSpecimen);
                cell.setStatus(CellStatus.EXTRA);
                cell.setInformation(Messages
                    .getString("DispatchReceiveScanDialog.cell.notInShipment.msg")); //$NON-NLS-1$
                pendingSpecimenNumber++;
            } else {
                if (DispatchSpecimenState.RECEIVED == state) {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
                } else if (DispatchSpecimenState.EXTRA == state) {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    if (extraSpecimens != null)
                        extraSpecimens.add(foundSpecimen.getId());
                    cell.setStatus(CellStatus.EXTRA);
                } else {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellStatus.IN_SHIPMENT_EXPECTED);
                }
            }
        }
    }

}
