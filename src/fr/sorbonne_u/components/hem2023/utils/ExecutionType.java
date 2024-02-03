package fr.sorbonne_u.components.hem2023.utils;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

// -----------------------------------------------------------------------------
/**
 * The enumeration <code>ExecutionType</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public enum				ExecutionType
{
	STANDARD,			// standard usage, no simulation
	UNIT_TEST,			// unit tests without simulation
	MIL_SIMULATION,		// model-in-the-loop simulation
	MIL_RT_SIMULATION,	// model-in-the-loop real time simulation
	SIL_SIMULATION;		// software-in-the-loop real time simulation

	public boolean		isStandard()
	{
		return this == STANDARD;
	}

	public boolean		isUnitTest()
	{
		return this == UNIT_TEST;
	}

	public boolean		isSimulated()
	{
		return this.isMIL() || this.isMILRT() || this.isSIL();
	}

	public boolean		isMIL()
	{
		return this == MIL_SIMULATION;
	}

	public boolean		isMILRT()
	{
		return this == MIL_RT_SIMULATION;
	}

	public boolean		isSIL()
	{
		return this == SIL_SIMULATION;
	}
}
// -----------------------------------------------------------------------------
