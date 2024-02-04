package fr.sorbonne_u.components.hem2023.utils;


import java.io.Serializable;

//-----------------------------------------------------------------------------
/**
* The class <code>MeasureI</code> mainly marks classes from which instance
* of measures are represented; the two major subclasses currently proposed
* represent scalar and compound measures (made of scalar measures), hence
* the presence of the method {@code isScalar()}.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>White-box Invariant</strong></p>
* 
* <pre>
* invariant	{@code true}	// no more invariant
* </pre>
* 
* <p>Created on : 2023-11-27</p>
* 
* @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
*/

public interface		MeasureI
extends 	TimingI,
			Serializable
{
	/**
	 * return true if this measure is scalar, false if it is composite.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this measure is scalar, false if it is composite.
	 */
	public boolean		isScalar();
}
// -----------------------------------------------------------------------------

