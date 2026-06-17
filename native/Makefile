BUILD_DIR ?= build
CMAKE     ?= cmake
TYPE      ?= Release

.PHONY: all clean

all: $(BUILD_DIR)/Makefile
	$(CMAKE) --build $(BUILD_DIR) --config $(TYPE)

$(BUILD_DIR)/Makefile:
	$(CMAKE) -S . -B $(BUILD_DIR) -DCMAKE_BUILD_TYPE=$(TYPE)

clean:
	$(CMAKE) --build $(BUILD_DIR) --target clean 2>/dev/null || true
	rm -rf $(BUILD_DIR)
