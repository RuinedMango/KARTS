# Kart model

Edit `kart.bbmodel` in Blockbench, then regenerate the Java layer:

```bash
python3 scripts/blockbench_to_java.py
sh gradlew build
```

The script writes `KartModelLayer.java`, which `KartModel` calls. Keep the group names unchanged unless you also update the child lookups and component maps in `KartModel`.

The Blockbench file uses a Y-up model and the converter applies the `Y_OFFSET` shift in `scripts/blockbench_to_java.py`.
