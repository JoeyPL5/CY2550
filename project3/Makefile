JAVAC = javac

SRCDIR = .
BINDIR = .

SOURCES = $(wildcard $(SRCDIR)/*.java)

CLASSES = $(patsubst $(SRCDIR)/%.java,$(BINDIR)/%.class,$(SOURCES))

all: $(CLASSES)

$(BINDIR)/%.class: $(SRCDIR)/%.java
	@mkdir -p $(BINDIR)
	$(JAVAC) -d $(BINDIR) $<

clean:
	rm -rf $(BINDIR)


