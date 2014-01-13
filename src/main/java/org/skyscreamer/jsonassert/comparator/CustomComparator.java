package org.skyscreamer.jsonassert.comparator;

import org.json.JSONException;
import org.skyscreamer.jsonassert.Customizable;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.util.Arrays;
import java.util.Collection;

public class CustomComparator extends DefaultComparator {

    private final Collection<Customizable> customizations;

	/**
	 * Create custom comparator from array of customizations.
	 * 
	 * @param mode
	 *            comparison mode
	 * @param customizations
	 *            array of customizations
	 */
    public CustomComparator(JSONCompareMode mode,  Customizable... customizations) {
        super(mode);
        this.customizations = Arrays.asList(customizations);
    }

	/**
	 * Create custom comparator from array of customizations. Provides backwards
	 * compatibility with code compiled against JSONassert 1.2.1 or earlier. Use
	 * CustomComparator(JSONCompareMode mode, Customizable... customizations)
	 * constructor in place of this one.
	 * 
	 * @param mode
	 *            comparison mode
	 * @param customizations
	 *            array of customizations
	 */
    @Deprecated
    public CustomComparator(JSONCompareMode mode,  Customization... customizations) {
        super(mode);
        this.customizations = Arrays.asList((Customizable[])customizations);
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {
        Customizable customization = getCustomization(prefix);
        if (customization != null) {
            if (!customization.matches(actualValue, expectedValue)) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else {
            super.compareValues(prefix, expectedValue, actualValue, result);
        }
    }

    private Customizable getCustomization(String path) {
        for (Customizable c : customizations)
            if (c.appliesToPath(path))
                return c;
        return null;
    }
}
