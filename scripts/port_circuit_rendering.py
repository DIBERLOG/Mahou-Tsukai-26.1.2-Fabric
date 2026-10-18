from pathlib import Path

root = Path(__file__).resolve().parents[1]
original = root/'recovered/stepsword/mahoutsukai'
target = root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'
for name in ['render/NewRenderUtils.java', 'render/MahoujinRenderType.java',
             'render/shader/PassThroughShaderInstance.java', 'render/shader/FogShaderInstance.java',
             'render/tile/ManaCircuitRenderer.java', 'render/tile/ManaCircuitMagitechRenderer.java',
             'render/tile/ManaCircuitRenderState.java']:
    text = (original/name).read_text()
    if name.endswith('MahoujinRenderType.java'):
        text = text[:text.index('   public static RenderType wrapGlowLayer(')] + '}\n'
        text = text.replace('RenderPipelines.TEXT\n      .toBuilder()', 'PipelineCopies.copy(RenderPipelines.TEXT)')
        text = text.replace('RenderPipelines.LINES.toBuilder()', 'PipelineCopies.copy(RenderPipelines.LINES)')
        text = text.replace('pipeline.toBuilder()', 'PipelineCopies.copy(pipeline)')
    if name.endswith('FogShaderInstance.java'):
        text = text.replace('import stepsword.mahoutsukai.render.tile.MahouRenderRL;', '')
        text = text.replace('MahouRenderRL.fog', 'Identifier.fromNamespaceAndPath("mahoutsukai", "textures/particle/depth.png")')
    text = text.replace('super.extractRenderState(', 'BlockEntityRenderer.super.extractRenderState(')
    out = target/name
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(text)
