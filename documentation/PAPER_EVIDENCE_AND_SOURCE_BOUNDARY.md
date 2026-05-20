# Paper Evidence And Source Boundary

## Paper

Row ID: `paper-2011-10`

Title: Evaluating The Effects Of I-35W Bridge Collapse On Road-Users In The Twin Cities

Citation: Xie, Levinson. (2011). Evaluating The Effects Of I-35W Bridge Collapse On Road-Users In The Twin Cities. Transportation Planning and Technology 34(7):691-703. https://doi.org/10.1080/03081060.2011.602850

## What The Paper Says It Used

The checked paper states that it adopted the Twin Cities Seven-County travel demand model, re-calibrated it against July 2007 loop-detector traffic data, and evaluated pre/post I-35W bridge collapse scenarios.

The methods section states that traffic assignment used stochastic user equilibrium, Dial's algorithm, and MSA. It also states that the code used by Davis and Sanderson for assignment had been translated from FORTRAN to Java and optimized. Calibration used July 2007 loop detector counts, selecting 63 stations with 166 detectors across the Twin Cities freeway system, and averaging real count data from 7:00 to 9:00 AM on Monday, Wednesday, and Friday in the last full week of July 2007.

## Local Evidence Checked

The local paper PDF and TeX source are readable:

`/Users/dlev2617/Documents/Papers/~05-Published/Transportation Planning and Technology/TPT-TRB09-I-35W-BridgeRegion/I-35W-TRB2009-BridgeRegion.pdf`

`/Users/dlev2617/Documents/Papers/~05-Published/Transportation Planning and Technology/TPT-TRB09-I-35W-BridgeRegion/I35WBridge.tex`

The broad I-35W project data folder is readable and includes OD, SpeedData, GIS, GPS, and survey subfolders:

`/Users/dlev2617/Documents/Data/~Nexus_Data/I-35WProject`

The likely SONG 2.0 Java/model source folder was identified:

`/Users/dlev2617/Documents/Data/~Nexus_Data/~CODE/SONG2_FINAL_ab/SONG_FINAL_b`

This folder includes Java files such as `SONG.java`, `TAssignment.java`, `TDistribution.java`, `TGeneration.java`, `DirectedGraph.java`, and `Investment.java`, plus small model/scenario files such as `stations.txt`, `ModifiedLinks.txt`, `Linkinfo_2005.txt`, `Linkinfo_2010.txt`, and related network inputs.

Hydration status was rechecked and confirmed complete. A curated staging copy was created in this row package:

- `code/song2_java/` (9 `.java` files)
- `data/model_inputs/` (36 `.txt` files)
- `metadata/STAGING_LOG.md` (include/exclude boundary note)

## Packaging Boundary

Package only the likely SONG 2.0 source and small model/scenario/calibration files needed to document the bridge-collapse model. Do not bulk-copy the multi-GB ODFile or SpeedData folders. Do not copy GPS or web-survey files for this row.

This row-level source-boundary staging is complete for the SONG package subset.
