package org.skyscreamer.jsonassert;

import static org.skyscreamer.jsonassert.Allowance.DISALLOWED;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class Behavior {
	private static class Builder {
		private Allowance extraFields;
		private Allowance anyArrayOrder;
		private final Collection<Customization> customizations = new HashSet<Customization>();

		private Builder(Behavior behavior) {
			this.extraFields = behavior.extraFields;
			this.anyArrayOrder = behavior.anyArrayOrder;
			this.customizations.addAll(behavior.customizations);
		}

		public static Builder from(Behavior behavior) {
			return new Builder(behavior);
		}

		public Builder withExtraFields(Allowance allowance) {
			this.extraFields = allowance;
			return this;
		}

		public Builder withAnyArrayOrder(Allowance allowance) {
			this.anyArrayOrder = allowance;
			return this;
		}

		public Builder add(Customization customization) {
			customizations.add(customization);
			return this;
		}

		public Behavior build() {
			return new Behavior(extraFields, anyArrayOrder, customizations);
		}
	}

	public static final Behavior STRICT = new Behavior(DISALLOWED, DISALLOWED, Collections.<Customization>emptySet());

	private final Allowance extraFields;
	private final Allowance anyArrayOrder;
	private final Collection<Customization> customizations;

	Behavior(Allowance extraFields, Allowance anyArrayOrder) {
		this(extraFields, anyArrayOrder, Collections.<Customization>emptySet());
	}

	private Behavior(Allowance extraFields, Allowance anyArrayOrder, Collection<Customization> customizations) {
		this.extraFields = extraFields;
		this.anyArrayOrder = anyArrayOrder;
		this.customizations = customizations;
	}

	public boolean extraFieldsAre(Allowance allowance) {
		return extraFields == allowance;
	}

	public boolean anyArrayOrderIs(Allowance allowance) {
		return anyArrayOrder == allowance;
	}

	public Behavior butAnyArrayOrderIs(Allowance allowance) {
		return Builder.from(this).withAnyArrayOrder(allowance).build();
	}

	public Behavior butExtraFieldsAre(Allowance allowance) {
		return Builder.from(this).withExtraFields(allowance).build();
	}

	public Behavior with(Customization customization) {
		return Builder.from(this).add(customization).build();
	}

    public Customization getCustomization(String path) {
        for (Customization c : customizations) {
            if (c.appliesToPath(path))
                return c;
        }
        return null;
    }
}
