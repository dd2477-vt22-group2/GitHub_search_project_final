import hashlib
import itertools
import os
import javalang
import glob


class Parser:
    """
    Parser class for Java files.
    """

    def __init__(self):
        self.tree = None
        self.classes = []
        self.methods = []
        self.all = []

    def parse(self, read_data):
        """
        Parse a java file and store the necessary information of classes and methods
        :param read_data: The java code read
        :return:
        """

        tree = javalang.parse.parse(read_data)
        self.tree = tree
        package_name = tree.package.name if hasattr(tree.package, 'name') else None
        classesAndInterfaces = itertools.chain(tree.filter(javalang.tree.ClassDeclaration),
                                               tree.filter(javalang.tree.InterfaceDeclaration))
        for _, node in classesAndInterfaces:
            start_line = node.position.line
            end_line = find_end_line_number(node)
            modifiers = node.modifiers if hasattr(node, 'modifiers') else None
            # if start_line == end_line, the body is empty
            # print(node.implements) if hasattr(node, 'implements') else None
            current_class = {
                'type': 'class',
                'package': package_name,
                'modifiers': modifiers,
                'name': node.name,
                'extends': node.extends.name + ("<" + ", ".join([a.type.name
                                                                if a.type is not None else a.pattern_type
                                                                if hasattr(a, 'pattern_type') else None
                                                                for a in node.extends.arguments]) + ">"
                                                if hasattr(node.extends, 'arguments')
                                                and node.extends.arguments is not None
                                                else ""
                                                )
                if node and hasattr(node, 'extends') and not isinstance(node.extends, list) and node.extends else
                node.extends[0].name + ("<" + ", ".join([a.type.name
                                                        if a.type is not None else a.pattern_type
                                                        if hasattr(a, 'pattern_type') else None
                                                        for a in node.extends[0].arguments]) + ">"
                                        if hasattr(node.extends[0], 'arguments')
                                        and node.extends[0].arguments is not None
                                        else ""
                                        )
                if node and hasattr(node, 'extends') and isinstance(node.extends, list) and node.extends[0]
                else None,
                'implements': [i.name + ("<" + ", ".join([a.type.name
                                                          if a.type is not None else a.pattern_type
                                                          if hasattr(a, 'pattern_type') else None
                                                          for a in i.arguments]) + ">"
                                         if hasattr(i, 'arguments') and i.arguments is not None else ""
                                         )
                               for i in node.implements]
                if node and hasattr(node, 'implements') and node.implements else None,
                'type_parameters': [t.name for t in node.type_parameters]
                if node and node.type_parameters else None,
                'fields': ([' '.join([' '.join(f.modifiers), f.type.name, ' '.join([n.name for n in f.declarators])])
                            for f in node.fields]) if node and node.fields else None,
                'method_names': [m.name for m in node.methods] if node and node.methods else None,
                'comments': node.documentation if node and node.documentation else None,
                'start_line': start_line,
                'end_line': end_line,
                'parameters': None,
                'return_type': None
            }
            self.classes.append(current_class)
            self.all.append(current_class)

        for _, node in tree.filter(javalang.tree.MethodDeclaration):
            start_line = node.position.line
            end_line = find_end_line_number(node)
            modifiers = node.modifiers if hasattr(node, 'modifiers') else None
            # print("return_type: " + str(node.return_type))
            # print("parameters: " + str(node.parameters))
            # if start_line == end_line, the body is empty
            current_method = {
                'type': 'method',
                'package': package_name,
                'modifiers': modifiers,
                'return_type': node.return_type.name + "<" + ", ".join([(a.type.name
                                                                         if a.type is not None else a.pattern_type
                                                                         if hasattr(a, 'pattern_type') else None)
                                                                        for a in node.return_type.arguments]) + ">"
                if node and node.return_type and hasattr(node.return_type, 'arguments')
                and node.return_type.arguments and node.return_type.arguments is not None else
                node.return_type.name
                if node and node.return_type else None,
                'name': node.name,
                'parameters': [p.type.name + "<" + ", ".join([(a.type.name
                                                               if a.type is not None else a.pattern_type
                                                               if hasattr(a, 'pattern_type') else None)
                                                              for a in p.type.arguments]) + ">" + " " + p.name
                               if p.type and hasattr(p.type, 'arguments') and p.type.arguments
                               and p.type.arguments is not None
                               else p.type.name + " " + p.name
                               for p in node.parameters]
                if node and node.parameters else None,
                'type_parameters': [p.name for p in node.type_parameters]
                if node and node.type_parameters else None,
                'throws': node.throws,
                'comments': node.documentation if node and node.documentation else None,
                'start_line': start_line, 'end_line': end_line,
                'fields': None
            }
            self.methods.append(current_method)
            self.all.append(current_method)

    def print_classes(self):
        """
        Print all classes
        :return:
        """
        for c in self.classes:
            print(c)

    def print_methods(self):
        """
        Print all methods
        :return:
        """
        for m in self.methods:
            print(m)

    def print_all(self):
        """
        Print all classes and methods
        :return:
        """
        for a in self.all:
            print(a)


def find_end_line_number(node):
    """Finds end line of a node."""
    max_line = node.position.line

    def traverse(to_traverse):
        if not hasattr(to_traverse, 'children'):
            return
        for child in to_traverse.children:
            if isinstance(child, list) and (len(child) > 0):
                for item in child:
                    traverse(item)
            else:
                if hasattr(child, '_position'):
                    nonlocal max_line
                    if child._position.line > max_line:
                        max_line = child._position.line
                        return

    traverse(node)
    return max_line


def test():
    """Test the parser"""
    directory = os.getcwd()
    for file in glob.glob(directory + "/**/*.java", recursive=True):
        with open(file, 'r') as f:
            print(file)
            try:
                read_data = f.read()
                parser = Parser()
                parser.parse(read_data)
            except Exception as e:
                print(e)
                print("Error processing file: " + file + ", skipping.")
                continue
            parser.print_all()


def parse_all(directory):
    """
    Parse all java files in the given directory recursively
    :param directory: the directory containing java files
    :return:
    """
    for file in glob.glob(directory + "/**/*.java", recursive=True):
        with open(file, 'r', encoding='UTF-8') as f:
            try:
                file_name = file.split('\\')[-1]  # change to '/' for linux or mac os
                file_path = '\\'.join(file.split('\\')[:-1])
                file_relative_path = file_path.replace(directory, '')
                read_data = f.read()
                parser = Parser()
                parser.parse(read_data)
            except Exception as e:
                print("Error processing file: " + file_relative_path + '\\' + file_name +
                      ", skipping it due to " + (str(e)
                                                 if not isinstance(e, javalang.parser.JavaSyntaxError)
                                                 else "JavaSyntaxError"))
                continue
            for i, a in enumerate(parser.all):
                doc_id = hashlib.md5(str.encode(file_path + '/' + file_name + '_' + str(i))).digest()
                yield {
                    # '_id': doc_id,
                    'name': a['name'],
                    # 'file_path': file_path,
                    # 'package': a['package'],
                    # 'type': a['type'],
                    'is_class': a['type'] == 'class',
                    # 'modifiers': a['modifiers'],
                    # 'type_parameters': a['type_parameters'],
                    'super_types': (a['extends'] + ' ' if a['extends'] is not None else '') +
                                   (' '.join(a['implements']) if a['implements'] is not None else '')
                    if a['type'] == 'class' and (a['extends'] is not None or a['implements'] is not None) else '',
                    'parameters': ' '.join(a['parameters']) if a['parameters'] is not None else '',
                    'return_type': a['return_type'] if a['return_type'] is not None else 'void',
                    # 'throws': a['throws'] if hasattr(a, 'throws') else None,
                    # 'fields': a['fields'] if hasattr(a, 'fields') else None,
                    # 'method_names': a['method_names'] if hasattr(a, 'method_names') else None,
                    'comments_and_fields': (a['comments'] if a['comments'] is not None else '') + ' ' +
                                           (' '.join(a['fields']) if a['fields'] is not None else '')
                    if a['comments'] is not None or a['fields'] is not None else '',
                    'file_name': file_relative_path + '\\' + file_name,
                    'file_start_line': a['start_line'],
                    'file_end_line': a['end_line']
                }


if __name__ == "__main__":
    test()
