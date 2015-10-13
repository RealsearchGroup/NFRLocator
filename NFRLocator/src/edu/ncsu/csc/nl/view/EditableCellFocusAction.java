package edu.ncsu.csc.nl.view;

import java.awt.event.*;
import javax.swing.*;

import edu.ncsu.csc.nl.GCController;

public class EditableCellFocusAction extends WrappedAction implements ActionListener
{
	private JTable table;

	/*
	 *  Specify the component and KeyStroke for the Action we want to wrap
	 */
	public EditableCellFocusAction(JTable table, KeyStroke keyStroke)
	{
		super(table, keyStroke);
		this.table = table;
	}

	/*
	 *  Provide the custom behaviour of the Action
	 */
	public void actionPerformed(ActionEvent e)
	{
		int originalRow = table.getSelectedRow();
		int originalColumn = table.getSelectedColumn();
		
		if ( (originalColumn +1) == table.getModel().getColumnCount() ) {
			//System.out.println("move to next item");
			GCController.getTheGCController().markCurrentSentenceAsTrained();
			ActionEvent ae = new ActionEvent(table, 1, "Next");
			GCController.getTheGCController().actionPerformed(ae);
			invokeOriginalAction( e );
			table.tableChanged(null);
			return;
		}

		invokeOriginalAction( e );

		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();

		//  Keep invoking the original action until we find an editable cell

		while (! table.isCellEditable(row, column) )
		{
			invokeOriginalAction( e );

			//  We didn't move anywhere, reset cell selection and get out.

			if (row == table.getSelectedRow()
			&&  column == table.getSelectedColumn())
			{
				table.changeSelection(originalRow, originalColumn, false, false);
				break;
			}

			row = table.getSelectedRow();
			column = table.getSelectedColumn();

			//  Back to where we started, get out.

			if (row == originalRow
			&&  column == originalColumn)
			{
				break;
			}
		}
	}
}